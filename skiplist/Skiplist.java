package skiplist;

import java.util.Random;

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

    public Node searchSkipList(int key) {

        Node n = head;

        while (n.below != null) {
            n = n.below;

            while (key >= n.next.key) {
                n = n.next;
            }
        }

        return n;
    }

    public Node insertNode(int key) {

        Node position = searchSkipList(key);
        Node q;

        int level = -1;
        int numberOfHeads = -1;

        if (position.key == key) {
            return position;
        }

        do {

            numberOfHeads++;
            level++;

            canIncreaseLevel(level);

            q = position;

            while (position.above == null) {
                position = position.prev;
            }
            position = position.above;

            q = insertAfterAbove(position, q, key);

        } while (random.nextBoolean() == true);

        return q;
    }

    public Node deleteNode(int key){
        Node nodeToDelete = searchSkipList(key);

        if(nodeToDelete.key != key){
            return null;
        }

        removeReferencesToNode(nodeToDelete);

        while(nodeToDelete != null){
            removeReferencesToNode(nodeToDelete);

            if (nodeToDelete.above != null){
                nodeToDelete = nodeToDelete.above;
            } else {
                break;
            }
        }

        return nodeToDelete;
    }

    private void removeReferencesToNode(Node nodetoDelete){
        Node afterNodeToDelete = nodetoDelete.next;
        Node beforeNodeToDelete = nodetoDelete.prev;

        beforeNodeToDelete.next = afterNodeToDelete;
        afterNodeToDelete.prev = beforeNodeToDelete;
    }

    private void canIncreaseLevel(int level) {
        if (level >= heightOfSkipList) {
            heightOfSkipList++;
            addAnotherLevel();
        }
    }

    private void addAnotherLevel() {
        Node newHead = new Node(NEG_INFINITY);
        Node newTail = new Node(POS_INFINITY);

        newHead.next = newTail;
        newHead.below = head;
        newTail.prev = newHead;
        newTail.below = tail;

        head.above = newHead;
        tail.above = tail;

        head = newHead;
        tail = newTail;
    }

    private Node insertAfterAbove(Node position, Node q, int key) {
        Node newNode = new Node(key);
        Node nodeBeforeNewNode = position.below.below;

        setBeforeAndAfterReferences(q, newNode);
        setAboveAndBelowReferences(position, key, newNode, nodeBeforeNewNode);

        return newNode;
    }

    private void setBeforeAndAfterReferences(Node q, Node newNode) {
        newNode.next = q.next;
        newNode.prev = q;

        q.next.prev = newNode;
        q.next = newNode;
    }

    private void setAboveAndBelowReferences(Node position, int key, Node newNode, Node nodeBeforeNewNode) {
        if (nodeBeforeNewNode != null) {
            while (true) {
                if (nodeBeforeNewNode.next.key != key) {
                    nodeBeforeNewNode = nodeBeforeNewNode.next;
                } else {
                    break;
                }
            }

            newNode.below = nodeBeforeNewNode.next;
            nodeBeforeNewNode.next.above = newNode;
        }

        if (position != null) {
            if (position.next.key == key) {
                newNode.above = position.next;
            }
        }
    }


    public void printSkipList() {
    StringBuilder sb = new StringBuilder();
    
    Node currentLevel = head;
    int level = heightOfSkipList;
    
    while (currentLevel != null) {
        sb.append("Level ").append(level).append(": ");
        
        Node currentNode = currentLevel;
        while (currentNode != null) {
            switch (currentNode.key) {
                case NEG_INFINITY -> sb.append("-∞");
                case POS_INFINITY -> sb.append("+∞");
                default -> sb.append(currentNode.key);
            }
            
            if (currentNode.next != null) {
                sb.append(" → ");
            }
            
            currentNode = currentNode.next;
        }
        
        sb.append("\n");
        currentLevel = currentLevel.below;
        level--;
    }
    
    System.out.println(sb.toString());
}
}
