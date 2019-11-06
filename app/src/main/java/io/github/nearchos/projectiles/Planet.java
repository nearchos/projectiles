package io.github.nearchos.projectiles;

public class Planet {

    private final String name;
    private final double gravity;

    private Planet(String name, double gravity) {
        this.name = name;
        this.gravity = gravity;
    }

    public String getName() {
        return name;
    }

    public double getGravity() {
        return gravity;
    }

    public static final Planet [] PLANETS = new Planet[] {
            new Planet("Mercury", 3.70d),
            new Planet("Venus", 8.87d),
            new Planet("Earth", 9.81d),
            new Planet("Moon", 1.62d),
            new Planet("Mars", 3.72d),
            new Planet("Jupiter", 24.79d),
            new Planet("Saturn", 10.44d),
            new Planet("Uranus", 8.69d),
            new Planet("Neptune", 11.15d)
    };
}