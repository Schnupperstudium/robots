package com.github.schnupperstudium.robots.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.github.schnupperstudium.robots.entity.Entity;
import com.github.schnupperstudium.robots.entity.Item;
import com.github.schnupperstudium.robots.world.Material;
import com.github.schnupperstudium.robots.world.World;
import com.github.schnupperstudium.robots.world.WorldLoader;

/**
 * Created by sigmar on 28.05.17.
 */
public final class MapFileParser implements WorldLoader {
	
	public MapFileParser() {
		
	}

	@Override
	public World loadWorld(InputStream is) throws IOException {
		try {
			return parseWorld(is);
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}

	public void parseWorld(String filename) throws FileNotFoundException, ParseException {
		parseWorld(new File(filename));
	}

	public World parseWorld(File file) throws FileNotFoundException, ParseException {
		try (Scanner scanner = new Scanner(file)) {
			return parseWorld(scanner);
		}
	}

	public World parseWorld(InputStream is) throws ParseException {
		try (Scanner scanner = new Scanner(is)) {
			return parseWorld(scanner);
		}
	}

	private World parseWorld(Scanner scanner) throws ParseException {
		return new Parser(scanner).parseFields();
	}

	private static class Parser {
		private int lineCount;
		private Scanner scanner;

		public Parser(Scanner scanner) {
			lineCount = 0;
			this.scanner = scanner;
		}

		private World parseFields() throws ParseException {
			// parse first line (board size)
			String[] fieldSizeLineSplit = readLine().split("x");
			int width;
			int height;
			try {
				width = Integer.parseInt(fieldSizeLineSplit[0]);
			} catch (NumberFormatException e) {
				throw new ParseException(lineCount, "Invalid width: " + fieldSizeLineSplit[0]);
			}
			try {
				height = Integer.parseInt(fieldSizeLineSplit[1]);
			} catch (NumberFormatException e) {
				throw new ParseException(lineCount, "Invalid height: " + fieldSizeLineSplit[1]);
			}

			// parse material aliases (if present)
			Map<String, Material> materialAliases = new HashMap<>();
			for (Material material : Material.values()) {
				materialAliases.put(material.name(), material);
				materialAliases.put(material.name().toLowerCase(), material);
			}
			for (String line = readLine(); !line.isEmpty(); line = readLine()) {
				String[] aliasSplit = line.split("\\s*[:=]\\s*");
				materialAliases.put(aliasSplit[1], materialAliases.get(aliasSplit[0]));
			}

			// parse board definition
			final World world = new World(width, height);
			for (int y = 0; y < height; y++) {
				String line;
				try {
					line = readLine().trim();
				} catch (ParseException e) {
					throw new ParseException(lineCount,
							String.format("Stopped at row %d of %d", y, height));
				}
				String[] fieldLineSplit = line.split("\\s+");

				if (fieldLineSplit.length < width) {
					throw new NoSuchElementException(
							String.format("Row %d has %d elements, but %d expected.", y,
									fieldLineSplit.length, width));
				}

				for (int x = 0; x < width; x++) {
					Material material = materialAliases.get(fieldLineSplit[x]);
					if (material == null) {
						throw new ParseException(lineCount,
								"Cannot find mapping for material alias: " + fieldLineSplit[x]);
					}
					world.getTile(x, y).setMaterial(material);
				}
			}

			// search for end of board definition (denoted by an empty line or eof)
			while (scanner.hasNextLine()) {
				String line = readLine();
				if (line.isEmpty()) {
					break;
				}
			}

			// read items
			if (scanner.hasNextLine()) {
				for (String line = readLine(); !line.isEmpty(); line = readLine()) {
					String[] itemLineSplit = line.split(" ");
					try {
						Class<?> clazz = Class.forName(itemLineSplit[0]);
						int x = Integer.parseInt(itemLineSplit[1]);
						int y = Integer.parseInt(itemLineSplit[2]);

						Class<? extends Item> itemClazz = clazz.asSubclass(Item.class);
						Item item = itemClazz.getConstructor().newInstance();

						if (x < 0 || x >= width) {
							throw new ParseException(lineCount, "x out of bounds");
						}
						if (y < 0 || y >= height) {
							throw new ParseException(lineCount, "y out of bounds");
						}

						world.getTile(x, y).setItem(item);
					} catch (ClassNotFoundException e) {
						throw new ParseException(lineCount,
								"class for item not found: " + itemLineSplit[0]);
					} catch (NumberFormatException e) {
						throw new ParseException(lineCount, "x or y parameter is not an int");
					} catch (InstantiationException e) {
						throw new ParseException(lineCount, "no default constructor for item");
					} catch (Exception e) {
						throw new ParseException(lineCount, e.getMessage());
					}

					if (!scanner.hasNextLine())
						break;
				}
			}

			// read entities
			if (scanner.hasNextLine()) {
				for (String line = readLine(); !line.isEmpty(); line = readLine()) {
					String[] itemLineSplit = line.split(" ");
					try {
						Class<?> clazz = Class.forName(itemLineSplit[0]);
						int x = Integer.parseInt(itemLineSplit[1]);
						int y = Integer.parseInt(itemLineSplit[2]);

						Class<? extends Entity> entityClazz = clazz.asSubclass(Entity.class);
						Entity entity = entityClazz.getConstructor().newInstance();

						if (x < 0 || x >= width) {
							throw new ParseException(lineCount, "x out of bounds");
						}
						if (y < 0 || y >= height) {
							throw new ParseException(lineCount, "y out of bounds");
						}

						world.getTile(x, y).setVisitor(entity);
						entity.setWorld(world);
					} catch (ClassNotFoundException e) {
						throw new ParseException(lineCount,
								"class for item not found: " + itemLineSplit[0]);
					} catch (NumberFormatException e) {
						throw new ParseException(lineCount, "x or y parameter is not an int");
					} catch (InstantiationException e) {
						throw new ParseException(lineCount, "no default constructor for item");
					} catch (Exception e) {
						throw new ParseException(lineCount, e.getMessage());
					}

					if (!scanner.hasNextLine())
						break;
				}
			}
			
			return world;
		}

		private String readLine() throws ParseException {
			try {
				String line = scanner.nextLine().trim();
				lineCount++;
				return line;
			} catch (NoSuchElementException | IllegalStateException e) {
				throw new ParseException(lineCount, "Input ended unexpectedly", e);
			}
		}

	}
}
