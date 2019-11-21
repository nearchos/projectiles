package io.github.nearchos.projectiles;

class Planet {

    private final String name;
    private final double gravity;
    private final String resourceName;

    Planet(final String name, final double gravity, final String resourceName) {
        this.name = name;
        this.gravity = gravity;
        this.resourceName = resourceName;
    }

    String getName() {
        return name;
    }

    double getGravity() {
        return gravity;
    }

    String getResourceName() {
        return resourceName;
    }

//    public static final Planet [] PLANETS = new Planet[] {
//            new Planet("Mercury",   3.70d, "mercury"),
//            new Planet("Venus",     8.87d, "venus"),
//            new Planet("Earth",     9.81d, "earth"),
//            new Planet("Moon",      1.62d, "moon"),
//            new Planet("Mars",      3.72d, "mars"),
//            new Planet("Jupiter",   24.79d, "jupiter"),
//            new Planet("Saturn",    10.44d, "saturn"),
//            new Planet("Uranus",    8.69d, "uranus"),
//            new Planet("Neptune",   11.15d, "neptune")
//    };
}