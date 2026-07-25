# Map Colouring with DFS

A JavaFX application that detects enclosed regions in a map image, builds a region-adjacency graph and colours neighbouring regions with different colours using depth-first search and backtracking.

## How it works

1. Load a map image containing dark region boundaries.
2. Classify pixels as boundaries or free space using a configurable threshold.
3. Detect connected regions with an iterative DFS flood-fill algorithm.
4. Ignore the exterior and very small noise regions.
5. Build an adjacency graph by inspecting neighbouring pixels.
6. Assign colours with recursive DFS backtracking so adjacent regions differ.
7. Render the coloured map and generate a diagnostic image with region IDs.

## Features

- Image loading through JavaFX FileChooser
- Connected-component detection
- Region adjacency graph construction
- Graph colouring with backtracking
- Diagnostic output containing region IDs and pixel counts
- Original and processed image comparison
- Detailed region and neighbour information

## Technology stack

- Java 21
- JavaFX 21
- Maven
- Java AWT image processing
- DFS, stacks, hash maps and adjacency sets

## Run locally

### Requirements

- JDK 21
- Maven 3.9 or newer

```bash
mvn clean javafx:run
```

For best results, use a map with clear, dark and fully closed boundaries.

## Core classes

```text
MainApp.java             JavaFX interface and file workflow
MapImageProcessor.java   Boundary detection, DFS, graph construction and colouring
Region.java              Region pixels, neighbours and assigned colour
FxImageUtils.java        BufferedImage to JavaFX conversion
```

## Algorithmic concepts demonstrated

- Connected-component labelling
- Iterative depth-first search
- Graph modelling from image data
- Constraint satisfaction through backtracking
- Pixel-level image manipulation

## Future improvements

- Add automatic boundary-threshold selection
- Add tests using generated maps
- Support manual palette selection
- Improve handling of anti-aliased or broken boundaries
- Translate the application interface and identifiers into English

## Author

**Diana Ciodolan**
