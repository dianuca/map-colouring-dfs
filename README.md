# Map Colouring with DFS

A JavaFX desktop application that detects enclosed regions in a map image, builds a region-adjacency graph and colours neighbouring regions using depth-first search and recursive backtracking.

The project combines image processing, graph modelling and constraint-solving algorithms in an interactive graphical interface.

## Screenshots

### Application Interface

The application displays the original map and the final coloured result side by side. It also provides diagnostic information about the detected regions and their neighbours.

![DFS map colouring result](screenshots/dfs-colouring-result.png)

### Original Map

The input is a black-and-white map in which dark lines represent region boundaries.

![Original map](screenshots/original-map.png)

### Region Detection

Each enclosed area is detected as an individual connected region and assigned a unique identifier. The diagnostic image also displays the number of pixels contained in every region.

![Detected map regions](screenshots/detected-regions.png)

## Features

- Load map images using a JavaFX file chooser
- Detect enclosed areas through connected-component analysis
- Ignore the exterior background and small noise regions
- Build an adjacency graph from neighbouring regions
- Colour the graph using DFS and recursive backtracking
- Ensure adjacent regions receive different colours
- Compare the original and processed images
- Generate a diagnostic image with region IDs and pixel counts
- Display detailed information about regions, colours and neighbours

## How It Works

1. The user selects a map image containing dark, fully closed boundaries.
2. Pixels are classified as either boundaries or free space using a configurable threshold.
3. An iterative DFS flood-fill algorithm detects connected regions.
4. The exterior area and very small regions are excluded.
5. The application examines neighbouring pixels to construct a region-adjacency graph.
6. A recursive backtracking algorithm assigns colours to the graph.
7. The result is rendered as a coloured image.
8. A diagnostic image is generated to visualise region IDs and pixel counts.

## Technology Stack

- Java 21
- JavaFX 21
- Maven
- Java AWT image processing
- BufferedImage and pixel-level manipulation
- Depth-first search
- Graph colouring
- Recursive backtracking
- Hash maps, sets and stacks

## Project Structure

```text
src/main/java/
├── MainApp.java
├── MapImageProcessor.java
├── Region.java
└── FxImageUtils.java
