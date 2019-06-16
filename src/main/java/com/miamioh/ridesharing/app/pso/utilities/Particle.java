package com.miamioh.ridesharing.app.pso.utilities;

import java.util.ArrayList;

public class Particle {
    private int id;
    private ArrayList<Integer> position;
    private ArrayList<Integer> particleBestPosition;
    private Velocity particleVelocity;

    Particle(int id, ArrayList<Integer> position) {
        this.id = id;
        this.position = position;
        particleBestPosition = new ArrayList<Integer>(position);
        particleVelocity = new Velocity(position.size());
    }

    void setPosition(ArrayList<Integer> position) {
        this.position = position;
    }

    ArrayList<Integer> getPosition() {
        return this.position;
    }

    void setVelocity(Velocity particleVelocity) {
        this.particleVelocity = new Velocity(
                particleVelocity.getParticleVelocity());
    }

    Velocity getParticleVelocity() {
        return this.particleVelocity;
    }

    void setParticleBestPosition(ArrayList<Integer> particleBestPosition) {
        this.particleBestPosition = new ArrayList<Integer>(particleBestPosition);
    }

    ArrayList<Integer> getParticleBestPosition() {
        return particleBestPosition;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return id + "\npos:" + position.toString() + "\npbest:"
                + particleBestPosition.toString() + "\nvelo:"
                + particleVelocity.toString();
    }
}