import javafx.util.Pair;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Graph {
    int[][] edges;

    List<Integer> enrollment;

    List<List<Integer>> students;

    int[] colors;   //stores last color assignment

    int numCourses = 0;
    int currentColor = 0;

    int[] heuristics;



    //edge[i][j] == no of students taking courses i+1 and j+1 together.

    //prob statement, if edge[i][j] is > 0, then schedule i and j so that they dont fall on same day and also keep as much gap as possible between them



    Graph(String courses, String students) {


        try {
            Scanner coursesScanner = new Scanner(new File(courses));
            Scanner studentsScanner = new Scanner(new File(students));

//            int numCourses = 0;


            enrollment = new ArrayList<>();

            while(coursesScanner.hasNext()) {

                numCourses++;

                String line = coursesScanner.nextLine();
                String[] course = line.split(" ");


                int enrolled = Integer.parseInt(course[1]);



                enrollment.add(enrolled);
            }





            edges = new int[numCourses][numCourses];

            this.students = new ArrayList<List<Integer>>();





            while(studentsScanner.hasNext()) {
                String line = studentsScanner.nextLine();
                String[] coursesArray = line.split(" ");

                this.students.add(new ArrayList<Integer>());

                for (int i = 0; i < coursesArray.length; i++) {


                    this.students.get(this.students.size() - 1).add(Integer.parseInt(coursesArray[i]) - 1);

                    for (int j = i + 1; j < coursesArray.length; j++) {

//                        if(i == j) continue;    //since course cant be conflicting with itself



                        //course data is 1 ordered, my edges matrix is 0 ordered, so adjusted here
                        int course1 = Integer.parseInt(coursesArray[i]) - 1;
                        int course2 = Integer.parseInt(coursesArray[j]) - 1;

                        edges[course1][course2] += 1;

                        edges[course2][course1] += 1;
                        //since edges are symmetrical


                    }
                }

            }


            colors = new int[edges.length];   //denotes what color value vertex at index i has, 0 means no color


        }

        catch(Exception e) {
            System.out.println(e);
        }


    }


    void printGraph() throws Exception {


        File out = new File("my-graph.txt");

        BufferedWriter bw = new BufferedWriter(new FileWriter(out));


        for(int i = 0; i < edges[0].length; i++) {
            for(int j = 0; j < edges[0].length; j++) {
                bw.write(edges[i][j] + ", ");
            }
            bw.write("\n");
        }


    }


    void outputSolution(String name) {

        try {


            File out = new File("outputs/" + name + ".txt");

            BufferedWriter bw = new BufferedWriter(new FileWriter(out));

            for(int i = 0; i < colors.length; i++) {

                bw.write(i + 1 + " " + colors[i] + "\n");
                bw.flush();



            }
            bw.close();

        }

        catch (Exception e) {
            System.out.println(e);
        }

    }



    List<Integer> adjacentVertices(int vertex) {

        List<Integer> list = new ArrayList<Integer>();
        for(int i = 0; i < edges.length; i++) {
            if(edges[vertex][i] > 0) {
                //edge exists between vertex and i
                list.add(i);
            }
        }

        return list;
    }


    //different funcs for graph coloring using diff heuristics


    void largestVertexHeuristicColoring(String heuristic){

        //heuristics can be "enrollment", "weighted-degree" or "degree"

        if(!heuristic.equalsIgnoreCase("enrollment") && !heuristic.equalsIgnoreCase("weighted-degree") && !heuristic.equalsIgnoreCase("degree")) {
            System.out.println("INVALID HEURISTIC VALUE");
            return;
        }




        heuristics = new int[edges.length];


        currentColor = 1;   //colors start at 1, this value = no. of colors used

        for(int i = 0; i < edges.length; i++) {
            //looping over vertices


            if(heuristic.equalsIgnoreCase("enrollment")) {
                heuristics[i] = enrollment.get(i);
                continue;
            }

            for(int j = 0; j < edges.length; j++) {
                //looping over edges of each vertex

                switch(heuristic) {
                    case "weighted-degree":
                        //need to find vertex with sum of weights of connected edges is max
                        //sum of row of a particular vertex gives val needed
                        heuristics[i] += edges[i][j];
                        break;

                    case "degree":
                        if(edges[i][j] > 0) {
                            heuristics[i]++;
                        }
                        break;

                    default:
                        break;
                }


            }
        }


        colorGraph(heuristic);

    }









    int findHighestSatVertex(List<Integer> notColored, int[] degrees) {

        //use colors and edges arrays and notColored list to figure this out

        //will be one of notColored list

        //saturation = no of adjacent vertices that are already colored, this is wrong
        //correct sat = no of unique colors that are adjacent to vertex


        int saturations[] = new int[edges.length];

        for(int vertex : notColored) {

            //loop though vertices that aren't colored

            List<Integer> list = new ArrayList<>();

            for(int j = 0; j < edges.length; j++) {
                //loop through edges of this vertex

                if(edges[vertex][j] > 0 && colors[j] > 0 && !list.contains(colors[j])) {
                    //vertex j is adjacent to this.vertex and is colored
                    list.add(colors[j]);
                    saturations[vertex]++;
                }

            }


        }

        //find max saturation, break ties using highest degree
        int maxSaturation = -1;
        int maxSaturationIndex = -1; //it is possible for highest saturation to be 0, if graph is disconnected


        for (int i = 0; i < saturations.length; i++) {

            //here i is vertex name

            if(notColored.contains(i)) {

                //only consider this vertex if its in not colored list
                if(saturations[i] > maxSaturation ) {
                    maxSaturation = saturations[i];
                    maxSaturationIndex = i;
                }
                else if (saturations[i] == maxSaturation && maxSaturationIndex != -1) {
                    //break tie using highest degree
                    if(degrees[i] > degrees[maxSaturationIndex]) {
                        maxSaturationIndex = i;
                    }
                }

            }



        }



        return maxSaturationIndex;

    }

    void dSaturColoring() {


        int[] degrees = new int[edges.length];


        currentColor = 1;   //colors start at 1, this value = no. of colors used

        for(int i = 0; i < edges.length; i++) {
            //looping over vertices


            for(int j = 0; j < edges.length; j++) {
                //looping over edges of each vertex

                if(edges[i][j] > 0) {
                    degrees[i] += 1;
                }
            }
        }


        //find vertex with max degree

        int maxDegree = 0;

        int maxDegreeIndex = -1;


        for(int i = 0; i < degrees.length; i++) {

            if(degrees[i] > maxDegree) {
                maxDegree = degrees[i];
                maxDegreeIndex = i;
            }
        }

        colors[maxDegreeIndex] = currentColor;

        List<Integer> notColored = new ArrayList<>();





        //add vertices to notColored list
        for(int i = 0; i < edges.length; i++) {

            if(i != maxDegreeIndex) {
                notColored.add(i);
            }

        }

        while(!notColored.isEmpty()) {
            int vertex = findHighestSatVertex(notColored, degrees);




            notColored.remove((Integer) vertex);


            //check adjacent vertex colors for acceptable colors


            List<Integer> adjacentColors = new ArrayList<>();

            //find adjacent vertices

            for(int i = 0; i < edges.length; i++) {

                if(edges[vertex][i] > 0) {
                    //i is an adjacent vertex to this.vertex
                    if(colors[i] > 0) {
                        adjacentColors.add(colors[i]);
                    }
                }

            }

            int color;


            for(color = 1; color <= currentColor; color++) {
                //colors are 1-indexed
                if(!adjacentColors.contains(color)) {
                    colors[vertex] = color;
                    break;
                }

            }

            if(color == currentColor + 1) {
                currentColor++;
                colors[vertex] = currentColor;

            }


        }


//        printStats();

//        outputSolution("dSatur");






    }

    void colorGraph(String heuristic) {

        //this takes global heuristic array and colors graph accordingly

        currentColor = 0;   //delete existing coloring


        PriorityQueue<Vertex> notColored = new PriorityQueue<Vertex>(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                return o2.heuristic - o1.heuristic;
            }
        });

        for(int i = 0; i < heuristics.length; i++) {

            //need to add both edge weight sum and index into pq
            notColored.add(new Vertex(i, heuristics[i]));
        }

        while(!notColored.isEmpty()) {
            Vertex vertex = notColored.poll();

            //check adjacent vertex colors for acceptable colors


            List<Integer> adjacentColors = new ArrayList<Integer>();

            //find adjacent vertices

            for(int i = 0; i < edges.length; i++) {
                if(edges[vertex.index][i] > 0) {
                    //i is an adjacent vertex to this.vertex
                    if(colors[i] > 0) {
                        adjacentColors.add(colors[i]);
                    }
                }

            }


            int color;

            for(color = 1; color <= currentColor; color++) {
                //colors are 1-indexed
                if(!adjacentColors.contains(color)) {
                    colors[vertex.index] = color;
                    break;
                }

            }

            if(color == currentColor + 1) {
                currentColor++;
                colors[vertex.index] = currentColor;

            }
        }


//        outputSolution(heuristic);

    }




    double calculatePenalty() {


        double penalty = 0;

        //penalties can be 16, 8, 4, 2, 1.

        //penalties are calculated on a per student basis

        for(List<Integer> student : students) {

            //student is list of courses this student takes


            //all pairs of courses
            for(int i = 0; i < student.size(); i++) {


                for(int j = i + 1; j < student.size(); j++) {

                    //finding which exams are adjacent to i

                    int distance = Math.abs(colors[student.get(i)] - colors[student.get(j)]);

                    if (distance < 6) {

                        double cost =  Math.pow(2, 5 -  distance);
                        penalty += cost;

                    }



                }




            }


            //adjacent courses only
//            for(int i = 0; i < student.size(); i++) {
//                int leastDistance = 99999;
//                int leastDistanceIndex = -1;
//
//                for(int j = i + 1; j < student.size(); j++) {
//
//                    //finding which exams are adjacent to i
//                    if(Math.abs(colors[student.get(i)] - colors[student.get(j)]) < leastDistance) {
//                        leastDistance = Math.abs(colors[student.get(i)] - colors[student.get(j)]);
//                        leastDistanceIndex = j;
//                    }
//
//
//                }
//                //we know that leastDistanceIndex is adjacent to i, so calculate penalty for these 2 and add to total penalty
//
//                if (leastDistance < 6) {
//
//                    double cost =  Math.pow(2, 5 -  leastDistance);
//                    penalty += cost;
//                }
//
//
//            }


        }






        return penalty / students.size();

    }

    double calculatePenalty(String solutionFile) throws Exception{



        Scanner solutionScanner = new Scanner(new File(solutionFile));

        colors = new int[numCourses];



        while(solutionScanner.hasNext()) {



            String line = solutionScanner.nextLine();
            String[] arr = line.split(" ");

            if(arr[0].equalsIgnoreCase("") && arr[1].equalsIgnoreCase("")) {

                int course = Integer.parseInt(arr[2]);
                int color = Integer.parseInt(arr[3]);
                colors[course - 1] = color;

            }

            else if(arr[0].equalsIgnoreCase("")) {
                int course = Integer.parseInt(arr[1]);
                int color = Integer.parseInt(arr[2]);
                colors[course - 1] = color;
            }

            else {

                int course = Integer.parseInt(arr[0]);
                int color = Integer.parseInt(arr[1]);
                colors[course - 1] = color;

            }




        }


        return calculatePenalty();


    }




    List<Integer> kempeChainInterchange(int start, int direction) {

        //start denotes which vertex to start from
        //direction indicates which edge to take from start vertex

        List<Integer> kempeChain = new ArrayList<>();
//        HashMap<Integer, Integer> kempeChain = new HashMap<>();



        //select random vertex first

        int color1 = colors[start];
        int color2;


        List<Integer> adjacents = adjacentVertices(start);

        if(adjacents.size() == 0) {

            return null;

        }

        color2 = colors[adjacents.get(direction)];

        if(color1 == color2) {

            System.out.println("BUG FOUND: color1 and color2 are same in kempe chain");
            System.out.println("Color1: "  + color1);
            System.out.println("Color2: "  + color2);

            return null;
        }

        //form kempe chain with color1 and color2

        //basically do bfs from start vertex


        Queue<Integer> q = new LinkedList<>();


        q.add(start);


        while(!q.isEmpty()) {

            //need a visited array: this is kempe chain list
            int vertex = q.poll();

            List<Integer> neighbors = adjacentVertices(vertex);

            for(int neighbor: neighbors) {

                if(colors[neighbor] == color1 || colors[neighbor] == color2) {

                    if (!q.contains(neighbor) && !kempeChain.contains(neighbor)) {
                        q.add(neighbor);


                    }

                }
            }

            //add to kempe chain

//            kempeChain.put(vertex, vertex);
            kempeChain.add(vertex);
        }


        //interchange colors within kempe chain

        for(int vertex: kempeChain) {

            if(colors[vertex] == color1) {
                colors[vertex] = color2;
            }
            else {
                colors[vertex] = color1;
            }
        }

//        return kempeChain;  //needed for reverting back to prev state
        return null;




    }

    List<Integer> kempeChainInterchangeRandom() {

        //takes random vertex

        int start = (int)( Math.random() * edges.length - 1);

        //take random direction

        int neighbors = adjacentVertices(start).size();

        int direction = (int)( Math.random() * neighbors - 1);


        return kempeChainInterchange(start, direction);


    }

    void revertKempeChainInterchange(List<Integer> kempeChain ) {



        int color1, color2;

        color1 = colors[kempeChain.get(0)];
        color2 = -1;


        //figure out the color2 in kempe chain
        for(int vertex: kempeChain) {

            if(colors[vertex] != color1) {
                color2 = colors[vertex];
                break;
            }


        }

        if(color2 == -1) {
            //bug, shouldnt reach here


            System.out.println("BUG FOUND");
            System.out.println("size of chain: " + kempeChain.size());
            return;
        }




        for(int vertex: kempeChain) {

            if(colors[vertex] == color1) {
                colors[vertex] = color2;
            }
            else {
                colors[vertex] = color1;
            }
        }


    }




    void pairSwap(int start) {

        //start from start vertex
        //find out neighboring colors
        //consider colors this.colors - neighbors.colors
        //find vertices in graph with considered colors
        //for vertices with considered colors check that they don't have start.color as neighbor and start.color as their own color
        //if prev condition match, replace start's colors with this vertex's color




        List<Integer> neighbors = adjacentVertices(start);


        List<Integer> neighborColors = new ArrayList<>();

        List<Integer> candidateVertices = new ArrayList<>();

        for(int neighbor: neighbors) {
            neighborColors.add(colors[neighbor]);
        }

        for(int i = 0; i < edges.length; i++) {

            //loop thru all vertices
            if(!neighborColors.contains(colors[i]) && colors[i] != colors[start]) {

                List<Integer> neighbors2 = adjacentVertices(i);

                boolean hasStartColorAsNeighbor = false;

                for(int neighbor: neighbors2) {
                    if(colors[neighbor] == colors[start]) {
                        hasStartColorAsNeighbor = true;
                        break;

                    }
                }

                if(!hasStartColorAsNeighbor) {

                    candidateVertices.add(i);



                }

            }

        }


        //select random vertex from candidate vertices and do pair swap

        if(!candidateVertices.isEmpty()) {
            int select = (int)( Math.random() * candidateVertices.size() - 1);

            //do pair swap
//          System.out.println("pair swapping between colors " + colors[start] + " and  color " + colors[i]);
            int temp = colors[start];
            colors[start] = colors[candidateVertices.get(select)];
            colors[candidateVertices.get(select)] = temp;

        }





    }

    void pairSwapRandom() {

        int start = (int)( Math.random() * edges.length - 1);

        pairSwap(start);
    }




    void printStats() {
        System.out.println("Timeslots: " + currentColor);
        System.out.println("Penalty: " + calculatePenalty());

    }

    void squeakyWheelOptimization(int moves) {

        if(heuristics == null) {
            heuristics = new int[edges.length];


        }


        for(int p = 0; p < moves; p++) {

            int[] oldColors = colors.clone();

            int oldCurrentColor = currentColor;


            int[] blames = new int[edges.length];

            for(int i = 0; i < edges.length; i++) {
//                if(colors[i] > thresholdColor) {

                    blames[i] = currentColor - colors[i];
//                }
            }

            for(int i = 0; i < edges.length; i++) {
                heuristics[i] = blames[i];
            }

            colorGraph("SWO");

//            System.out.println("currentColor: " + currentColor);

//            hillClimbRandom(10,10);
            kempeChainInterchangeRandom();


        }








    }



    void hillClimb(int moves, boolean printProgress) {
        //this is systematic always best move hill climb


        //do kempe chain interchange to find neighbors of current solution


        //func repeats above for "moves" times to go to best solution

        //when stuck, does pair swap



        for(int i = 0; i < moves; i++) {

            if(printProgress) System.out.println("starting MOVE " + (i + 1));



            double minPenalty = calculatePenalty();

            double oldPenalty = minPenalty;



            int minVertex = -1;
            int minJ = -1;

            List<Pair<Integer, Integer>> done = new ArrayList<>();
            //keeps track of which pair of colors were already considered for prev kempe chain constructions


            for(int vertex = 0; vertex < edges.length; vertex++) {


                List<Integer> neighbors = adjacentVertices(vertex);


                for(int j = 0; j < neighbors.size(); j++) {


                    if(

                            done.contains(new Pair<Integer, Integer>(colors[vertex], colors[neighbors.get(j)])) ||
                            done.contains(new Pair<Integer, Integer>(colors[neighbors.get(j)], colors[vertex]))
                            ) {

                        continue;
                    }


                    done.add(new Pair<Integer, Integer>(colors[vertex], colors[neighbors.get(j)]));

                    int[] oldColors = colors.clone();


                    List<Integer> kempeChain = kempeChainInterchange(vertex, j);


                    double penalty = calculatePenalty();


                    if(penalty < minPenalty) {
                        minPenalty = penalty;
                        if(printProgress) System.out.println(": a lower penalty value: "  + minPenalty + " found within "+  j + 1  + " tries");



                        minVertex = vertex;
                        minJ = j;




                    }

                    colors = oldColors.clone();
                }
            }


            if(minVertex >= 0) {
                kempeChainInterchange(minVertex, minJ);
                if(printProgress) System.out.println("new penalty value: " + calculatePenalty());

            }






            //pair swap start
            if(Math.abs(oldPenalty - calculatePenalty()) < 0.0001) {
                //probably stuck in local optimum, so do pair swap as stuck in another local optmimum
                //do pair swap
                double penalty = calculatePenalty();

                for(int k = 0; k < 100; k++) {
                    int[] oldColors = colors.clone(); //don't need oldColors prev value here, so safely overwriting


                    pairSwapRandom();

                    double newPenalty = calculatePenalty();

                    if(newPenalty < penalty) {
                        penalty = newPenalty;
                        if(printProgress) System.out.println("Pair swap improved penalty to " + newPenalty + " after " + (k + 1) + " tries");

                    }


                    else {
                        colors = oldColors.clone();
                    }




                }

                //pair swap end
            }




        }



        if(printProgress) System.out.println("penalty: " + calculatePenalty());


//        outputSolution("dSatur");
    }

    void hillClimbRandom(int moves, int tries, boolean printProgress) {

        //this is random first best move hill climb

        //do kempe chain interchange to find neighbors of current solution, then move to first least penalty value found within say "tries" interchanges


        //func repeats above for "moves" times to go to best solution



        for(int i = 0; i < moves; i++) {

//            System.out.println("calculating penalty");

            double minPenalty = calculatePenalty();
            double oldPenalty = minPenalty;

            int minVertex = 0;
            int minJ = 0;




            for(int j = 0; j < tries; j++) {

                int[] oldColors = colors.clone();



                List<Integer> kempeChain = kempeChainInterchangeRandom();



                double penalty = calculatePenalty();


                if(penalty < minPenalty) {
                    minPenalty = penalty;
                   if(printProgress) System.out.println("MOVE " + i +  ": a lower penalty value: "  + minPenalty + " found within "+  (j + 1)  + " tries");


                    break;
                }
                else {
                    colors = oldColors.clone();

                }





            }
            //pair swap start



            if(Math.abs(oldPenalty - calculatePenalty()) < 0.0001) {


                //probably stuck in local optimum, so do pair swap as stuck in another local optimum
                //do pair swap
                    double penalty = calculatePenalty();

                for(int k = 0; k < tries; k++) {
                    int[] oldColors = colors.clone(); //don't need oldColors prev value here, so safely overwriting


                    pairSwapRandom();

                    double newPenalty = calculatePenalty();

                    if(newPenalty < penalty) {
                        penalty = newPenalty;
                        if(printProgress) System.out.println("Pair swap improved penalty to " + newPenalty + " after " + (k + 1) + " tries");
                        break;

                    }


                    else {
                        colors = oldColors.clone();
                    }




                }


            }
            //pair swap end


            if(printProgress) System.out.println("penalty: " + calculatePenalty());



        }







//        outputSolution("dSatur");
    }
}
