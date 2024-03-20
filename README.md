# Predictions

Predictions is a simulations system that creates a virtual world where entities interact based on a predefined set of rules and properties. It predicts the outcomes of these interactions, allowing you to explore and understand the consequences of different connections between the entities. This simulation provides valuable insights into how various factors and relationships play out, helping you analyze and make informed decisions in a controlled environment.

## Key Parameters

### 1. World
The main framework of the simulation comprising entities, environment variables, a list of rules, and termination conditions.

### 2. Entity
A group of individuals within the world defined by name, quantity, and properties.

### 3. Property
A group of variables describing an entity, with types including integer, float, boolean, and string.

### 4. Environment Variables
External factors influencing the simulation, affecting entities’ behaviors, interactions, and overall evolution.

### 5. Rules
Dynamic components driving the simulation by altering entities’ properties and interactions over time, with optional secondary entities.

### 6. Termination Conditions
Determines how long the simulation should run, based on seconds, ticks, or user's choice.

## Program Process

1. **Upload XML File:**
    Select an XML file describing the world.

2. **Tabs:**
   - **Details Tab:**
     - Display information about the world, rules, entities, and environment variables.
   - **New Execution Tab:**
     - Choose values for environment variables and population.
     - Start simulations and transfer to the Results tab.

   - **Results Tab:**
     - View information about simulations.
     - Monitor running simulations.
     - View results of finished simulations, including entities by ticks and properties statistics.

   - **Control Buttons:**
     - Pause, resume, and stop simulations during execution.

   - **Rerun Button:**
     - Rerun simulations with preserved values.

## Modules, Packages, and Classes

### 1. UI Module
   - Responsible for graphical application interactions.
   - Divided into Controllers for each tab.

### 2. Data Transfer Module
   - Transfers data between UI and Engine to maintain OOP encapsulation.

### 3. Engine Module
   - Manages passive operations for simulations.
   - Runs simulations and handles logic.
   - Reads XML files, translating data into project classes.

