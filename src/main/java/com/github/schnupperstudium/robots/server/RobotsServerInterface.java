package com.github.schnupperstudium.robots.server;

import java.util.List;

import com.github.schnupperstudium.robots.RobotsInterface;

public interface RobotsServerInterface extends RobotsInterface {
	List<String> listLevels();
}
