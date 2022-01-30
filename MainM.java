import java.io.*;
import java.util.*;
public class MainM {
    //the registers, the memory, an array to hold job names is created
    // an array for the hard coded Psiars is created, new list data structure that handles the job queue is created
    int PSIAR;
    int SAR;
    int SDR;
    int TMPR;
    int CSIAR;
    int ACC;
    int timer;
    int trueTime;
    int memory[] = new int[1020];
    String jobList[]=new String[6];
    int setPsiars[]=new int[]{20,250,400,500,600,700};
    int arrivalT[]=new int[]{0,10,15,20,18,40};
    int jobsNDone[]=new int[]{1,1,1,1,1,1};
    PCB nList=new PCB();
    String outPutFile="";


    public void Init_Sys()
    {
        //values for the registers and arrays are created
        PSIAR=0;
        SAR=0;
        SDR=0;
        TMPR=0;
        CSIAR=0;
        ACC=0;





    }
    public void Load_Programs()
    {
        //this loop fills the name array, fills the list and loads all programs into memory
        for(int i=0;i< jobList.length;i++)
        {
            //name array fill
            jobList[i]="src/Resource/sharkosprog"+(i+1);
            System.out.println(jobList[i]+" has been loaded.");
            outPutFile+=jobList[i]+" has been loaded.\n";
            int instructionAdd=setPsiars[i];
            File programs= new File(jobList[i]);
            String MemoryI="";
            // tries for exisitng text files
            try {
                Scanner newRead = new Scanner(programs);
                //reads through text files and gathers instructions
                while (newRead.hasNextLine()) {

                    String job = newRead.nextLine();
                    String[] jobH= job.split(" ");
                    //instructions are read as strings and a number is assgined to a new string value based on op code
                    //and operand.
                    for (String t:jobH) {
                        if (t.equals("ADD")) {
                            MemoryI = "10";
                        }
                        else if(t.equals("SUB")){
                            MemoryI = "20";
                        }
                        else if(t.equals("LDA")){
                            MemoryI = "30";
                        }
                        else if(t.equals("STR")){
                            MemoryI = "40";
                        }
                        else if(t.equals("BRH")){
                            MemoryI = "50";
                        }
                        else if(t.equals("CBR")){
                            MemoryI = "60";
                        }
                        else if(t.equals("LDI")){
                            MemoryI = "70";
                        }
                        else if(t.equals("HALT")){
                            MemoryI = "80";
                        }
                        else
                            MemoryI=MemoryI.concat(t);
                    }
                    //instructions are stored in memory
                    memory[instructionAdd]=Integer.parseInt(MemoryI);

                    instructionAdd++;
                }

                newRead.close();
            }catch(FileNotFoundException e)
            {
                System.out.println("Error");
                e.printStackTrace();
            }
        }
        System.out.println("-------------");
        outPutFile+="\n-------------------------\n";
    }
    public void RUN_Shark()
    {
        //programs are loaded
        Load_Programs();

        int instruction;
        String inst;

       // first loop begins this loop checks for jobs in the syste aka PCBs in the list
        do
        {
            boolean working = true;
            //PSIAR and ACC are changed


            do
            {

                for(int j=0;j<arrivalT.length;j++) {
                    if (trueTime == arrivalT[j]){
                        //list fill based ona arrival time
                        //note there needs to be one job that arrives at 0
                        outPutFile+="\n-------------------------\n";
                        System.out.println("-------------");
                        System.out.println("The program "+jobList[j]+" has arrived at time "+trueTime);
                        outPutFile+="The program "+jobList[j]+" has arrived at time "+trueTime+"\n";
                        nList.insertAtEnd(setPsiars[j], setPsiars[j], jobList[j], 0, arrivalT[j]);
                        outPutFile=nList.printQStatus(nList,outPutFile);
                        outPutFile+="\n-------------------------\n";
                        System.out.println("-------------");
                    }
                }

                PSIAR=nList.head.data2;
                ACC=nList.head.acc;
                Boolean Yield= false;

                //yields on 6 instructions
                if (timer==6)
                {
                    Yield= INT_HAND(false);
                }

                if (Yield==true)
                    break;
                //memory is fetched from memory
                instruction=memory[PSIAR];

                inst=String.valueOf(instruction);

                //timer++;

                //instruction is decoded
                CSIAR=Integer.parseInt(inst.substring(0,2));

                if (CSIAR!=80) {
                    SDR = Integer.parseInt(inst.substring(2));
                }
                else
                    SDR=0;
               // timer++;
                //csiar op codes
                switch(CSIAR)
                {
                    //goes into ADD
                    case 10:
                        ADD();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into SUB
                    case 20:
                        SUB();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into LDA
                    case 30:
                        LDA();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into STR
                    case 40:
                        STR();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into BRH
                    case 50:
                        BRH();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into CBR
                    case 60:
                        CBR();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into LDI
                    case 70:
                        LDI();
                        nList.head.data2=PSIAR;
                        nList.head.acc=ACC;
                        break;
                    //goes into HALT
                    case 80:
                        working=HALT();
                        break;
                }
                //timer goes up to reach yield
                timer++;
                trueTime++;
                outPutFile+="Timer: "+trueTime+"\n";
                System.out.println("Timer: "+trueTime);

             //while check if there are still instructions for current job to be done
            }while(working==true);

        //while check if there are still jobs to be done by checking the head of the list
        }while(jobsExist()==true);

    }

    public Boolean INT_HAND(Boolean jobState){
        //interrupt handler is called with a boolean that tells it if there are still jobs to be done or if the
        //system should quit

        Boolean jobS =jobState;
        Boolean Yield;
                if(jobS==false) {
                //Yield shows queue status
                   //shows current job
                    outPutFile+="\n-------------------------\n";
                    System.out.println("-------------");
                    System.out.println("The current job: " + nList.head.name + " has timed out after " + timer + " instructions.");
                    outPutFile+="The current job: " + nList.head.name + " has timed out after " + timer + " instructions.\n";

                   //shows the PSIAR and the ACC saved to perserve integrity
                    System.out.println("Saved ACC: "+nList.head.acc+" to job: "+nList.head.name);
                    outPutFile+="Saved ACC: "+nList.head.acc+" to job: "+nList.head.name+"\n";

                    System.out.println("Saved PSIAR: "+nList.head.data2);
                    outPutFile+="Saved PSIAR: "+nList.head.data2+"\n";

                    //moves the current head to the back and the next head is chosen
                    nList.head = nList.moveQ(nList.head, nList.head.next);

                    //shows new PSIAr and new ACC
                    System.out.println("New PSIAR: "+nList.head.data2);
                    outPutFile+="New PSIAR: "+nList.head.data2+"\n";

                    System.out.println("Loaded ACC: "+nList.head.acc+" to job: "+nList.head.name);
                    outPutFile+="Loaded ACC: "+nList.head.acc+" to job: "+nList.head.name+"\n";

                    System.out.println("You have yielded the CPU to: " + nList.head.name);
                    System.out.println("-------------");
                    outPutFile+="\n-------------------------\n";

                    //timer is reset
                    timer = 0;
                    Yield = true;
                    trueTime++;
                }
                else
                {
                    //interrput when job ends

                    outPutFile+="\n-------------------------\n";
                    System.out.println("-------------");
                    System.out.println("A job has ended...");
                    outPutFile+="A job has ended...\n";

                    System.out.println("Continuing to the next job...");
                    outPutFile+="Continuing to the next job...\n";

                    outPutFile+="\n-------------------------\n";
                    System.out.println("-------------");

                    //remove the job from the queue by removing head completely
                    nList.head=nList.removeHead(nList.head);
                    for(int i=0;i<jobsNDone.length;i++){
                        if(jobsNDone[i]==1)
                        {
                            jobsNDone[i]=0;
                            break;
                        }
                    }

                   //registers reset
                    Yield = false;
                    trueTime++;
                }

        return Yield;

    }
    public boolean jobsExist()
    {
        //checks if jobs exits by checking the list
        int jobs=0;

        for (int i=0;i< jobsNDone.length;i++)
        {
            if(jobsNDone[i]==1)
            {
                jobs++;
            }
        }

        if (nList.head!=null&&(jobs!=0))
        {
            return true;
        }
            else
                return false;
    }
    public void ADD()
    {
        //add function
        TMPR=ACC;
        ACC=PSIAR+1;
        PSIAR=ACC;
        ACC=TMPR;
        TMPR=SDR;
        SAR=TMPR;
        SDR=memory[SAR];
        TMPR=SDR;
        ACC=ACC+TMPR;
        CSIAR=0;
    }
    public void SUB()
    {
        //sub function
        TMPR = ACC;
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = TMPR;
        TMPR = SDR;
        SAR = TMPR;
        SDR=memory[SAR];
        TMPR = SDR;
        ACC =ACC - TMPR;
        CSIAR = 0;

    }
    public void LDA()
    {
        //lda function
        ACC = PSIAR + 1;
        PSIAR = ACC;
        TMPR = SDR;
        SAR = TMPR;
        SDR=memory[SAR];
        ACC = SDR;
        CSIAR = 0;

    }
    public void STR()
    {
        //str function
        TMPR = ACC;
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = TMPR;
        TMPR = SDR;
        SAR = TMPR;
        SDR = ACC;
        memory[SAR]= SDR;
        CSIAR = 0;


    }
    public void BRH()
    {
        //Brh function
        PSIAR=SDR;
        CSIAR=0;
    }
    public void CBR()
    {
        //cbr function
        if (ACC==0)
        {

            PSIAR=SDR;
            CSIAR=0;

        }
        else {
            TMPR = ACC;
            ACC = PSIAR + 1;
            PSIAR = ACC;
            ACC = TMPR;
            CSIAR=0;

        }
    }
    public void LDI()
    {
        //ldi functio
        ACC = PSIAR + 1;
        PSIAR = ACC;
        ACC = SDR;
        CSIAR = 0;

    }
    public boolean HALT()
    {
        //halt prints last job registers, name and the amount of insturctions complete
        System.out.println("The last record on the registers before the job halted:");
        outPutFile+="The last record on the registers before the job halted: \n";

        System.out.println("PSIAR: "+PSIAR+" SAR: "+SAR+" SDR: "+SDR+" TMPR: "+TMPR+" CSIAR: "+CSIAR+" ACC: "+ACC);
        outPutFile+="PSIAR: "+PSIAR+" SAR: "+SAR+" SDR: "+SDR+" TMPR: "+TMPR+" CSIAR: "+CSIAR+" ACC: "+ACC+"\n";

        System.out.println("The current job: " + nList.head.name + " has ended with "+  (nList.head.data2 -nList.head.data) +" instructions done.");
        outPutFile+="The current job: " + nList.head.name + " has ended with "+  (nList.head.data2 -nList.head.data) +" instructions done.\n";

        INT_HAND(true);
        return false;
    }
    public void EXIT_SHARK()
    {
        END();
        System.exit(0);
    }
    public void END()
    {
        //print out of all the expected memory affected by the program.
        System.out.println("Results of program 1");
        System.out.println("Memory at "+100+" was filled by "+memory[100]);
        System.out.println("Memory at "+101+" was filled by "+memory[101]);
        System.out.println("Memory at "+102+" was filled by "+memory[102]);
        System.out.println("Memory at "+103+" was filled by "+memory[103]);

        outPutFile+="Results of program 1\n"
        +"Memory at "+100+" was filled by "+memory[100]+"\n"
        +"Memory at "+101+" was filled by "+memory[101]+"\n"
        +"Memory at "+102+" was filled by "+memory[102]+"\n"
        +"Memory at "+103+" was filled by "+memory[103]+"\n";

        System.out.println("Results of program 2");
        System.out.println("Memory at "+900+" was filled by "+memory[900]);
        System.out.println("Memory at "+901+" was filled by "+memory[901]);
        System.out.println("Memory at "+903+" was filled by "+memory[903]);

        outPutFile+="Results of program 2\n"
                +"Memory at "+900+" was filled by "+memory[900]+"\n"
                +"Memory at "+901+" was filled by "+memory[901]+"\n"
                +"Memory at "+903+" was filled by "+memory[903]+"\n";

        System.out.println("Results of program 3");
        System.out.println("Memory at "+200+" was filled by "+memory[200]);

        outPutFile+="Results of program 3\n"
                +"Memory at "+200+" was filled by "+memory[200]+"\n";

        System.out.println("Results of program 4");
        System.out.println("Memory at "+203+" was filled by "+memory[203]);
        System.out.println("Memory at "+201+" was filled by "+memory[201]);
        System.out.println("Memory at "+202+" was filled by "+memory[202]);

        outPutFile+="Results of program 4\n"
                +"Memory at "+203+" was filled by "+memory[203]+"\n"
                +"Memory at "+201+" was filled by "+memory[201]+"\n"
                +"Memory at "+202+" was filled by "+memory[202]+"\n";

        System.out.println("Results of program 5");
        System.out.println("Memory at "+301+" was filled by "+memory[301]);
        System.out.println("Memory at "+302+" was filled by "+memory[302]);
        System.out.println("Memory at "+303+" was filled by "+memory[303]);
        System.out.println("Memory at "+304+" was filled by "+memory[304]);

        outPutFile+="Results of program 5\n"
                +"Memory at "+301+" was filled by "+memory[301]+"\n"
                +"Memory at "+302+" was filled by "+memory[302]+"\n"
                +"Memory at "+303+" was filled by "+memory[303]+"\n"
                +"Memory at "+304+" was filled by "+memory[304]+"\n";

        System.out.println("Results of program 6");
        System.out.println("Memory at "+305+" was filled by "+memory[305]);


        outPutFile+="Results of program 6\n"
                +"Memory at "+305+" was filled by "+memory[306]+"\n";

        outPutFile+="\n-------------------------\n";
        System.out.println("Memory has been emptied");
        outPutFile+="Memory has been emptied";


        try {
            File file = new File("C:\\Users\\Micheal\\Desktop\\OS1 Design Pro\\outputfile.txt");
            if (file.createNewFile()) {

                FileWriter writer = new FileWriter(file);
                writer.write(outPutFile);
                writer.close();
            }
            else
                System.out.println("File found");                ;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
