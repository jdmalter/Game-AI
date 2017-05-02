Author:
Jacob Malter (jdmalter@ncsu.edu)

Build:
Create an eclipse project.

Run:
Run the main method inside app.Main to open a list of options of code to run. To run any behavior,
click on its respective button. It is possible to close each processing window without closing Main.

Packages, Files, & Descriptions:
app
    AppButton
        A base class for a consistent look between buttons.
    ClassesPanel
        Renders a list of buttons to start by a processing applet.
    Main
       Provides a list of buttons for ClassesPanel to render and start by a processing applet.
behaviortree
    Composite
        Defines sequential, random, and parallel selector and sequence behavior trees.
    Decorator
        Defines finite and infinite selector and sequence behavior trees.
    Tree
        Represents a behavior tree.
decisiontree
    Tree
        Represents a decision tree.
digraph
    Digraph
        A mathematical structure that consists of two sets of elements: a set of
        vertices and a set of edges. Edges are weighted ordered pairs of vertices.
    HashDigraph
        A graph implementation using a hash map of vertices to a hash map of adjacent vertices to weights.
digraphproblem
    DigraphProblem
        A graph based problem where the objective is to find a path from the initial vertex to the goal vertex.
domain
    Domain
        A set of source points where all points have visibility between each other.
    FloorGraph
        A HashDigraph that calculates edge values by a weight function.
drawing
    Breadcrumb
        Provides a stateless representation to draw a breadcrumb.
    Character
        Provides a stateless representation to draw a character.
    Drive
        Provides a stateless representation to draw a drive bar.
    SmallGraph
        Provides a stateless representation to draw a digraph.
    SourcePoint
        Provides a stateless representation to draw a target.
finiteautomaton
    Decider
        Decides whether a finite automata accepts a string.
    DeterministicFiniteAutomaton
        A deterministic finite automaton whose transition function defines a constant
        output state from states for every pair of state in states and symbol in alphabet.
    FiniteAutomaton
        A finite automaton can be defined by five components: a state set, an
        alphabet, a transition function, an initial state, and a goal set.
    NondeterministicFiniteAutomaton
        A non-deterministic finite automaton whose transition function defines a some
        (possibly changing) output state(s) from states for some pairs of state in
        states and symbol in alphabet.
function
    HigherOrder
        Provides higher order functions.
    Procedure
        Represents an operation that accepts no arguments and returns no result.
        Unlike most other functional interfaces, Procedure is expected to
        operate via side-effects.
    QuadConsumer
        Represents an operation that accepts four input arguments and returns no result. This is
        the four-arity specialization of Consumer. Unlike most other functional interfaces,
        QuadConsumer is expected to operate via side-effects.
    TriFunction
        Represents a function that accepts three arguments and produces a result.
        This is the three-arity specialization of function.
kinematic
    Kinematic
        A basic, immutable, two dimensional representation of a kinematic data structure.
    Matching
        Provides linear and angular position matching behaviors.
    Mutation
        Provides kinematic setting, restriction, and update functions.
learning
    Model
        A basic, immutable model for a regression.
    Models
        Provides operations to compute information gain from attributes on model.
    Recording
        Records the behavior tree algorithm.
problem
    Expander
        Provides node expansion function.
    Node
        An immutable structure of four components: state, action, parent, and path cost.
    Problem
        A problem can be defined by five components: initial state, actions,
        transition model, goal test, and path cost.
    Solver
        Provides node solution function.
queuesearch
    GraphSearch
        A queue search that stores explored nodes.
    PriorityQueueSearch
        A priority queue search that stores explored nodes.
    QueueSearch
        An abstract search using a queue of leaf nodes available for expansion.
screen
    DecisionMaking
        A base class for decision making.
    IndoorEnvironment
        This class was created to remove specific graph creation from general decision making.
    Movement
        A base class for movement. Additionally, this class handles adding
        characters, bucketing characters, and dropping breadcrumbs.
    Screen
        A base class for consistent look and basic operations between screens.
    jacob's grid thing
        The background image for the indoor envrionment.
screendecisionmaking
    BehaviorTree
        Runs the behavior tree algorithm.
    DecisionTree
        Runs the decision tree algorithm.
    Learning
        Applies the decision tree learning algorithm.
screenmovement
    ArriveSteering
        Runs the arrive steering algorithm.
    BasicMotion
        Runs the kinematic motion algorithm.
    FlockingBehavior
        Runs the flocking behavior algorithm.
    WanderSteering
        Runs the wander steering algorithm.
screenpathfinding
    PathFinding
        Runs the A* pathfinding algorithm and a pathfollowing algorithm.
search
    AStarSearch
        An informed priority queue search using an A* evaluation function.
    BestFirstSearch
        An abstract informed priority queue search using an evaluation function.
    DijkstraSearch
        An informed priority queue search using an A* evaluation function whose
        heurisitic always returns zero.
    Search
        A functional interface for search.
searchfunction
    Evaluation
        Represents a function that accepts one node and produces the estimated cost
        of the cheapest solution through the given node.
    Factory
        Provides constants and out of the box heuristics.
    Heuristic
        Represents a function that accepts one state and produces the estimated cost
        of the cheapest solution from the given state.
sequence
    Sequence
        A basic, immutable, persistent, generic representation of a logical list.
    Sequences
        Provides empty, count, seq, conj, stream, concat, and reverse operations on sequences.
steering
    Blender
        Provides steering weighted blending function.
    FixedRadiusNearestNeighbor
        Provides bucketing and neighbor functions.
    Matching
         Provides linear and angular position matching behaviors.
    Movement
        Provides steering wander, pursue, and avoid functions.
    Mutation
        Provides steering setting, restriction, and update functions.
    Steering
        A basic, immutable, two dimensional representation of a steering data structure.
target
    Factory
        Provides constants and factory functions.
    Predicate
        Provides satisfaction and decceleration predicates.
    Target
        A basic, immutable, unitless, two dimensional representation of a target data structure.
utility
    Mathf
        Provides float constants, exponentiation functions, and trigonometric functions.
    Pair
        A basic, immutable representation of two non-null objects.
    Random
        Provides random float, random binomial, and random color functions.
    Streams
        Provides a stream of natural numbers.
vector
    Arithmetic
        Provides dot, addition, subtraction, multiplication, and division functions.
    Factory
        Provides constants, factory functions, mapping functions, and application functions.
    Property
        Provides direction, magnitude, and unit vector functions.
    Streams
        Provides vector stream functions.
    Vector
        A basic, immutable, unitless, two dimensional representation of magnitude and direction.
