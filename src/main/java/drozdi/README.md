### TODO

Performance:
    chunk images for static walls (wall, ladder)
    map loader to map[][] and create and generate only 

Errors:
    Downloaded look ok, but it is currupted
    multiplayer dont accept UDP request like they should 


### ChatGPT

What is bigger issue, that I paint image for every wall or that I have object for every wall?
ChatGPT

The bigger issue between painting an image for every wall or having an object for every wall depends on the specific context and goals of your game. Both issues have their own implications:

    Painting an Image for Every Wall:
        Memory Efficiency: If you paint an image for every wall, you'll consume memory for each image, which can be memory-intensive, especially if you have a large number of walls. This can lead to increased memory usage and potential performance issues.
        Rendering Efficiency: Rendering individual images for each wall can be less efficient, as it involves drawing many separate images. It may lead to a performance hit, especially if the number of walls is significant.

    Having an Object for Every Wall:
        Memory Usage: Each wall object consumes memory for its properties and methods. If you have a large number of wall objects, this can increase memory usage, but typically not as much as loading individual images for each wall.
        Object Management: Managing a large number of objects can have some overhead in terms of CPU usage. Creating and updating many objects can impact performance.

To find the right balance, you can consider the following approaches:

    Tile-Based Rendering: As previously mentioned, you can use a tile-based approach where you load a single image for a wall type and render the map using a 2D array. This is memory-efficient and usually more rendering-efficient because you're rendering a smaller number of larger images.

    Object Pooling: If you have many wall objects that are frequently created and destroyed, you can consider implementing object pooling. This way, you reuse wall objects instead of creating and destroying them, which can reduce the overhead of object management.

    Lazy Loading: If you have objects that are not always on the screen, consider loading them only when they are about to become visible and unloading them when they go out of view. This can help manage memory efficiently.

The choice between painting images for walls or having objects for walls depends on your game's requirements, including the number of walls, the game's performance goals, and the specific gameplay mechanics. In many cases, a combination of these approaches may be the best solution to strike a balance between memory and rendering efficiency.
