def grandparent = project.parent
while (grandparent.parent != null) {
	grandparent = grandparent.parent
}
project.properties.setProperty('grandparent.version', grandparent.version)