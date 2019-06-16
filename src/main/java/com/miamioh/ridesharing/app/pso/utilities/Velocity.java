package com.miamioh.ridesharing.app.pso.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Velocity {
    ArrayList<Pair<Integer>> particleVelocity = new ArrayList<Pair<Integer>>();

    /**
     * <p>
     * Constructs random Velocity 'vector' for a given number of cities in a
     * graph.
     * </p>
     *
     * @param numberOfCities
     *            number of cities in a graph
     */
    Velocity(int numberOfCities) {
        // TODO: Purely random velocity vector initialization seems to be less
        // beneficial than initialization based on empty vector
        /*
         * List<Integer> citiesPermutation1 = new ArrayList<Integer>();
         * List<Integer> citiesPermutation2 = new ArrayList<Integer>(); for (int
         * i = 0; i < numberOfCities; i++) { citiesPermutation1.add(i);
         * citiesPermutation2.add(i); } Collections.shuffle(citiesPermutation1,
         * new Random()); Collections.shuffle(citiesPermutation2, new Random());
         *
         * ArrayList<Pair<Integer>> particleVelocity = new
         * ArrayList<Pair<Integer>>(); for (int i = 0; i < numberOfCities; i++)
         * { particleVelocity.add(new Pair<Integer>(citiesPermutation1.get(i),
         * citiesPermutation2.get(i))); }
         */
        ArrayList<Pair<Integer>> particleVelocity = new ArrayList<Pair<Integer>>();

        this.particleVelocity = particleVelocity;
    }

    /**
     * <p>
     * Constructs Velocity based on initialParticleVelocity list.
     * </p>
     *
     * @param initialParticleVelocity
     *            initial particle's velocity
     */
    Velocity(ArrayList<Pair<Integer>> initialParticleVelocity) {
        for (Pair<Integer> pair : initialParticleVelocity) {
            if (!pair.getLeft().equals(pair.getRight())) {
                particleVelocity.add(pair);
            }
        }
    }

    /**
     * <p>
     * Constructs a new Velocity by subtracting second position from first
     * position.
     * </p>
     *
     * <p>
     * Constructed velocity is equivalent to the velocity that applied to
     * position1 gives position2.
     * </p>
     *
     * @param position1
     *            minuend position
     * @param position2
     *            subtrahend position
     */
    Velocity(ArrayList<Integer> position1, ArrayList<Integer> position2) {
        for (int i = 0; i < position2.size(); i++) {
            int toReplace = (int) position2.get(i);
            int j = i;
            while (!position2.get(j).equals(position1.get(i)))
                j++;
            Collections.swap(position2, i, j);

            if (toReplace != position2.get(i)) {
                particleVelocity.add(new Pair<Integer>(new Integer(toReplace),
                        new Integer(position2.get(i))));
            }
        }
    }

    /**
     * <p>
     * Multiplies this Velocity by coefficient given by the parameter.
     * </p>
     *
     * <p>
     * Multiplication by coefficient consists of three cases, depending on the
     * value of coeff:
     * </p>
     * <ul>
     * <li>If c = 0 then c*v gives an empty Velocity, represented by null value</li>
     * <li>Else if c in (0, 1] then v is contracted to the length of floor(c*v)</li>
     * <li>Else if c > 1 then c*v = v + v + ... + v + c'v, where c' in (0, 1]. v
     * is added floor(c) times.</li>
     * </ul>
     *
     * @param coeff
     *            coefficient by which this Velocity is multiplied
     * @return Velocity modified by multiplication
     */
    Velocity multiplyBy(double coeff) {
        if (coeff == 0.0) {
            particleVelocity = null;
        } else if (coeff > 0.0 && coeff <= 1.0) {
            int startFrom = (int) Math.floor(coeff
                    * (double) particleVelocity.size());
            for (int i = startFrom; i < particleVelocity.size(); i++) {
                particleVelocity.remove(particleVelocity.size() - 1);
            }
        } else {
            double coeffFloor = Math.floor(coeff);
            Velocity velocityToAdd = new Velocity(new ArrayList<Pair<Integer>>(
                    getParticleVelocity()));

            ArrayList<Pair<Integer>> remainderToAdd = new ArrayList<Pair<Integer>>(
                    particleVelocity);

            for (int i = 0; i < (int) coeffFloor; i++) {
                add(velocityToAdd);
            }

            int startFrom = (int) Math.floor((coeff - coeffFloor)
                    * (double) remainderToAdd.size());
            int finishAt = remainderToAdd.size();
            for (int i = startFrom; i < finishAt; i++) {
                remainderToAdd.remove(startFrom);
            }
            add(new Velocity(remainderToAdd));
        }
        return this;
    }

    /**
     * <p>
     * Multiplies this Velocity by coefficient coeff multiplied by random value
     * from (0, 1]. Works the same as multiplyBy method.
     * </p>
     *
     * @param coeff
     * @return Velocity modified by multiplication
     */
    Velocity multiplyRandomlyBy(double coeff) {
        double randomAddition = (new Random()).nextDouble();
        if (randomAddition == 0.0)
            randomAddition = 1.0;

        return multiplyBy(coeff * randomAddition);
    }

    /**
     * <p>
     * Performs addition of this Velocity and that Velocity.
     * </p>
     *
     * <p>
     * Addition creates the list of transpositions (represented by Pair<T> type)
     * which contains first the ones of this Velocity, followed by the ones of
     * that Velocity.
     * </p>
     *
     * @param that
     *            Velocity to add to this Velocity
     * @return Velocity modified by addition
     */
    Velocity add(Velocity that) {
        // TODO: add method should perform contraction (within the meaning of
        // the PSO approach) before returning the result, for optimization
        particleVelocity.addAll(that.getParticleVelocity());
        return this;
    }

    /**
     * <p>
     * Applies this Velocity to the given position and returns newly obtained
     * position.
     * </p>
     *
     * @param position
     *            particle's position
     * @return newly obtained position
     */
    ArrayList<Integer> applyOn(ArrayList<Integer> position) {
        for (Pair<Integer> pair : particleVelocity) {
            for (int i = 0; i < position.size(); i++) {
                Integer element = position.get(i);
                if (element.equals(pair.getLeft()))
                    position.set(i, pair.getRight());
                else if (element.equals(pair.getRight()))
                    position.set(i, pair.getLeft());
            }
        }
        return position;
    }

    /**
     * <p>
     * Returns ArrayList of Integer Pair objects representing particle's
     * velocity.
     * </p>
     *
     * @return list of pairs representing particle's velocity
     */
    ArrayList<Pair<Integer>> getParticleVelocity() {
        return particleVelocity;
    }

    @Override
    public String toString() {
        return particleVelocity.toString();
    }
}