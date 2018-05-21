package com.github.schnupperstudium.robots.world;

import java.io.IOException;
import java.io.InputStream;

public interface WorldLoader {
	World loadWorld(InputStream is) throws IOException;
}
