//PCB class that helps creates the jobs
 class PCB {
    Node head; // head of list
     static class Node {
        int data;
        int data2;
        int acc;
        int aTime;
        String name;
        Node next;

        Node(int d,int t,String n,int a, int at) {
            this.data = d;
            this.data2=t;
            this.name=n;
            this.acc=a;
            this.aTime=at;
            next = null;
        } // Constructor
    }
    //inserts jobs into the queue in a fixed order
    public void insertAtEnd(int p, int p2, String n,int a,int at)
    {
        Node new_node = new Node(p,p2,n, a,at);


        if (head == null)
        {
            head = new Node(p,p2,n,a,at);
            return;
        }

        new_node.next = null;
        Node last = head;
        while (last.next != null)
            last = last.next;

        last.next = new_node;
        return;
    }
    //removes the head to simulate a job ending
    public Node removeHead(Node head)
    {
        if(head==null)
            return null;
        Node temp=head;
        head=head.next;
        return head;

    }

    //moves the head reference from the current head to its next and then moves the old head to the back
    public Node moveQ(Node oldHead, Node newHead)
    {
        if(oldHead==null||newHead==null)
            return oldHead;
        Node temp=oldHead;
        Node temp2=newHead;

        while(temp.next!=null){

            temp=temp.next;
        }

        temp.next=oldHead;
        oldHead.next=null;
        return newHead;
    }

    //prints status of the queue/list
    public String printQStatus(PCB main,String oF)
    {
            Node currNode = main.head;

            System.out.print("Current jobs in queue: ");
            oF+="Current jobs in queue: ";
            // Traverse through the LinkedList
            while (currNode != null) {
                // Print the data at current node
                if(currNode.next!=null) {
                    System.out.print(currNode.name + " -> ");
                    oF+=currNode.name + " -> ";
                }
                else {
                    System.out.print(currNode.name);
                    oF+=currNode.name;
                }
                    // Go to next node
                currNode = currNode.next;
            }
            oF+="\n";
          System.out.println("");
    return  oF;
    }
}