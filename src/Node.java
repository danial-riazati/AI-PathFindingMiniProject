import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Node implements Comparable<Node> {
    public int i, j;

    //for bfs&dfs
    boolean visited = false;

    // Parent in the path
    public Node parent = null;

    public List<Edge> neighbors;

    public int g = 0;
    // heuristic function
    public int h;

    public int f() {
        return this.g + this.h;
    }

    Node(int h, int i, int j) {
        this.h = h;
        this.i = i;
        this.j = j;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public int compareTo(Node n) {
        if (this.f() != n.f())
            return this.f() - n.f();
        else if (this.i != n.i)
            return this.i - n.i;
        else return this.j - n.j;
    }

    public static int hCalculator(int i1, int j1, int i2, int j2, int ROW,
                                  int COL) {
        return Math.min(Math.abs(i1 - i2), ROW - Math.abs(i1 - i2))
                + Math.min(Math.abs(j1 - j2), COL - Math.abs(j1 - j2));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Node)) {
            return false;
        }
        Node n = (Node) o;
        return this.i == n.i && this.j == n.j;
    }

    //class with node & the cost of g for each branches
    public static class Edge {
        Edge(int weight, Node node) {
            this.weight = weight;
            this.node = node;
        }

        public int weight;
        public Node node;
    }

    public void addBranch(int weight, Node node) {
        Edge newEdge = new Edge(weight, node);
        neighbors.add(newEdge);
    }

    public static Node aStar(Node start, Node target) {
        TreeSet<Node> closedList = new TreeSet<>();
        TreeSet<Node> openList = new TreeSet<>();

        openList.add(start);

        while (!openList.isEmpty()) {
            Node n = openList.first();
            if (n.equals(target)) {
                return n;
            }
            openList.remove(n);
            closedList.add(n);
            for (Node.Edge edge : n.neighbors) {
                Node m = edge.node;
                if (closedList.contains(m)) {
                    continue;
                }
                if (!openList.contains(m)) {
                    m.parent = n;
                    m.g = n.g + 1;
                    openList.add(m);
                    continue;
                }
                if (m.g > n.g + 1) {
                    m.g = n.g + 1;
                    m.parent = n;
                }
            }
        }
        return null;
    }

    public static Node bfs(Node start, Node target) {
        Queue<Node> queue = new PriorityQueue<>();
        queue.add(start);
        start.visited = true;
        while (!queue.isEmpty()) {
            Node x = queue.poll();
            for (int i = 0; i < x.neighbors.size(); i++) {
                if (!x.neighbors.get(i).node.visited) {
                    x.neighbors.get(i).node.visited = true;
                    x.neighbors.get(i).node.parent = x;
                    queue.add(x.neighbors.get(i).node);
                    if (x.neighbors.get(i).node.equals(target))
                        return x.neighbors.get(i).node;
                }
            }
        }
        return null;
    }

    public static Node dfs(Node start, Node target) {
        Stack<Node> queue = new Stack<>();
        queue.add(start);
        start.visited = true;
        while (!queue.isEmpty()) {
            Node x = queue.pop();
            for (int i = 0; i < x.neighbors.size(); i++) {
                if (!x.neighbors.get(i).node.visited) {
                    x.neighbors.get(i).node.visited = true;
                    x.neighbors.get(i).node.parent = x;
                    queue.add(x.neighbors.get(i).node);
                    if (x.neighbors.get(i).node.equals(target))
                        return x.neighbors.get(i).node;
                }
            }
        }
        return null;
    }

    public static void print(Node target, int ROW, int COL) {
        Node n = target;

        if (n == null) {
            System.out.println("blocked");
            return;
        }

        List<List<Integer>> ids = new ArrayList<>();

        while (n.parent != null) {
            List<Integer> x = new ArrayList<>();
            x.add(n.i);
            x.add(n.j);
            ids.add(x);
            n = n.parent;
        }
        List<Integer> x = new ArrayList<>();
        x.add(n.i);
        x.add(n.j);
        ids.add(x);
        Collections.reverse(ids);
        x = ids.get(0);
        for (List<Integer> id : ids) {
            if (!x.equals(id)) {
                if (id.get(1) - x.get(1) == 1 || (id.get(1) == 0 && x.get(1) == COL - 1))
                    System.out.print("Right ");
                else if (id.get(0) - x.get(0) == 1 || (id.get(0) == 0 && x.get(0) == ROW - 1))
                    System.out.print("Down ");
                else if (id.get(1) - x.get(1) == -1 || (id.get(1) == COL - 1 && x.get(1) == 0))
                    System.out.print("Left ");
                else if (id.get(0) - x.get(0) == -1 || (id.get(0) == ROW - 1 && x.get(1) == 0))
                    System.out.print("Up ");
                x = id;

            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        //matrix in file added
        File f = new File("C://Users//DNR//Desktop//file.txt");
        Scanner scanner = new Scanner(f);
        ArrayList<String> data = new ArrayList<>();
        while (scanner.hasNextLine()) {
            data.add(scanner.nextLine());
        }
        String[][] block = new String[data.size()][];
        for (int i = 0; i < data.size(); i++) {
            block[i] = data.get(i).split(" ");
        }
        int ROW = block.length;
        int COL = block[0].length;
        int srcI = 0;
        int srcJ = 0;
        int dstI = 0;
        int dstJ = 0;
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[0].length; j++) {
                if (block[i][j].equals("S")) {
                    srcI = i;
                    srcJ = j;
                } else if (block[i][j].equals("A")) {
                    dstI = i;
                    dstJ = j;
                }
            }
        }
        Node head = new Node(hCalculator(srcI, srcJ, dstI, dstJ, ROW, COL), srcI, srcJ);
        head.g = 0;
        Node target = new Node(0, dstI, dstJ);


        // print table
        for (String[] strings : block) {
            for (int l = 0; l < COL; l++) {
                System.out.print(strings[l]);
            }
            System.out.println();
        }

        Node[][] x = new Node[ROW][COL];
        for (int k = 0; k < ROW; k++) {
            for (int l = 0; l < COL; l++) {
                if (block[k][l].equals("0")) {
                    x[k][l] = new Node(hCalculator(k, l, dstI, dstJ, ROW, COL), k, l);
                }
            }
        }
        x[srcI][srcJ] = head;
        x[dstI][dstJ] = target;
        for (int k = 0; k < ROW; k++) {
            for (int l = 0; l < COL; l++) {
                if (!block[k][l].equals("1")) {
                    if (!block[(k - 1 + ROW) % ROW][l].equals("1")) {
                        x[k][l].addBranch(1, x[(k - 1 + ROW) % ROW][l]);
                    }
                    if (!block[(k + 1 + ROW) % ROW][l].equals("1")) {
                        x[k][l].addBranch(1, x[(k + 1 + ROW) % ROW][l]);
                    }
                    if (!block[k][(l - 1 + COL) % COL].equals("1")) {
                        x[k][l].addBranch(1, x[k][(l - 1 + COL) % COL]);
                    }
                    if (!block[k][(l + 1 + COL) % COL].equals("1")) {
                        x[k][l].addBranch(1, x[k][(l + 1 + COL) % COL]);
                    }
                }
            }
        }
        System.out.println("_____Choose the algorithm : (1.DFS/2.BFS/3.a*)____");
        Scanner sc = new Scanner(System.in);
        int a = sc.nextInt();
        if (a == 1) {
            Node res = dfs(head, target);
            print(res, ROW, COL);
        } else if (a == 2) {
            Node res = bfs(head, target);
            print(res, ROW, COL);
        } else if (a == 3) {
            Node res = aStar(head, target);
            print(res, ROW, COL);
        }


    }
}