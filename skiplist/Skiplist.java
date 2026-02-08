package skiplist;

public class Skiplist {

    private Node head;
    private Node tail;

    private final int NEG_INFINITY = Integer.MIN_VALUE;
    private final int POS_INFINITY = Integer.MAX_VALUE;

    private int heightOfSkipList = 0;

    public Random random = new Random();

    public Skiplist() {

        head = new Node(NEG_INFINITY);
        tail = new Node(POS_INFINITY);

        head.next = tail;
        tail.prev = head;
    }

    public Node SearchSkipList(int key){

        Node n = head;

        while (n.below != null) {
            n = n.below;

            while (key >= n.next.key){
                n = n.next;
            }
        }

        return n;
        
    }
}
