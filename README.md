# LISO

Lucene Image Search with Opencv, an open framework for image retrieval.

The lib can be used for building an image search engine. It provides convinient interfaces to define your own algorithms of feature extraction and distance measurements.

This project is under developing.

# Intro

## Content

package: 
-pers.tank.liso.
  - factory.
    - distances.Distance: an interface for defining distance measurements.
    - features.Feature: an interface for defining image feature extraction methods.
    - Transforms: a tool class for color space.
  - index.Indexer: used for creating index documents by user's requirment.
  - search.searcher: used for indexing.
  
## Dependencies

- OpenCV310 for java
- Lucene6.3
- JDK 1.8

---
Published on Jun 8, 2017
Created by Derek Liu
