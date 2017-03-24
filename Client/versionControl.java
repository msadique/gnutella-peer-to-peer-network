/*******************************************************************************
 *  FILE NAME    : updateThread.java
 *
 *  DESCRIPTION  : his class invoke its constructor when its object is created, 
 a value one second is passed for checking the status of the file at the client side whether 
 it has undergone any change. If it has undergone any change then it update the file status at the server side as well. 
 *
 *
 ******************************************************************************/


/*******************************************************************************
 *          HEADER FILES
 ******************************************************************************/

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
 import java.util.StringTokenizer;


public class versionControl {
	Timer timer;
    
	public versionControl(int seconds) {    //in constructor make list of files in input folder
        timer = new Timer();
	System.out.println("Hello");
        timer.schedule(new RemindTask2(),0, seconds*1000);
	timer.schedule(new RemindTask3(),0,1000);
	}

	// this class runs in every second set by client
    class RemindTask2 extends TimerTask {
    	public void run() {
	    vControl();
	}
    }
    class RemindTask3 extends TimerTask {
    	public void run() {
	    vRefress();
	}
    }
	public void vRefress(){
	for(int i =0;i<Client.versionList.size();i++)
	if(Client.versionList.get(i).getAuthor().equals("Slave"))
	{
		if(Client.versionList.get(i).getCTTR()<1)
		{
			Client.versionList.get(i).setCTTR(Client.versionList.get(i).getTTR());
			Query query = new Query();
			query.setVersion(Client.versionList.get(i).getVersion());
		        query.setFileName(fileName);
         		query.setIpAddr(Client.ClientIP);
         		query.setPort(Client.ClientPort);
         		query.setQueryType(QueryType.Refress);
               		try
                	{
                        	{
                               	Socket socket=new Socket(Client.versionList.get(i).getIpAddr(),Client.versionList.get(i).getPort());
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(query);
                                socket.close();
                        }
                	}
                	catch(Exception e)
                	{
                	//        System.out.println(e.getMessage());
               		 }
                
		}
		else
		{
			Client.versionList.get(i).getCTTR((Client.versionList.get(i).getCTTR()-1));
		}

	}	
	
}
    public void vControl()
    {
	int mainFlag=0;
	System.out.println("----SAM----");
	Path currentRelativePath = Paths.get("");
	String cpath = currentRelativePath.toAbsolutePath().toString();
	 try {
            FileReader fileReader = new FileReader("fileVersion.txt");
   	    Client.versionList2.clear();
            //PrintWriter writer = new PrintWriter("fileVersion.txt", "UTF-8");
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = new BufferedReader(fileReader);
	    String line;
			String fileName,author;
            while((line = bufferedReader.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(line);
                while (st.hasMoreElements()) {
                    Version ver = new Version();
                    ver.setFileName((String)st.nextElement());
                    ver.setAuthor((String)st.nextElement());
					ver.setTime(Long.parseLong((String)st.nextElement()));
					ver.setVersion(Double.parseDouble((String)st.nextElement()));
					ver.setTTR(Integer.parseInt((String)st.nextElement()));
					ver.setValid(Integer.parseInt((String)st.nextElement()));
					ver.setIpAddr((String)st.nextElement());
					ver.setPort(Integer.parseInt((String)st.nextElement()));
					ver.setCTTR(ver.getTTR());
                    		Client.versionList2.add(ver);
                }
            }
			// writer.close();
            // Always close files.
            bufferedReader.close();
            // Process p = Runtime.getRuntime().exec("mv file.txt "+fileName);
            File clientFolder= new File(cpath+"/input_files/");
            File[] clientFiles=clientFolder.listFiles();
            for(File file:clientFiles)
            {
				int flag=1;
	//			if(file.getName().startsWith("."))System.out.println("---Not include----"+file.getName());
				if (!(file.getName().startsWith("."))){
				for(int i=0;i<Client.versionList2.size();i++)
				{
                    if( file.isFile()&& file.getName().equals( Client.versionList2.get(i).getFileName() ))
                    {
						if(Client.versionList2.get(i).getTime() !=  (file.lastModified()/1000))
						{
							Client.versionList2.get(i).setTime(file.lastModified()/1000);
							Client.versionList2.get(i).setVersion((Client.versionList2.get(i).getVersion()+.1));
							flag=0;
							mainFlag=1;
							invalidateSlaves(file.getName());// Invalidation
							break;
						}
						else
						{
							flag=0;
							break;
						}
					}
				}
				if(flag==1)
				{
					Version ver = new Version();
					ver.setFileName(file.getName());
                    			ver.setAuthor("Master");
					ver.setTime(file.lastModified()/1000);
					ver.setVersion(1.0);
					ver.setTTR(60);
					ver.setValid(1);
					ver.setIpAddr(Client.ClientIP);
					ver.setPort(Client.ClientPort);
							
                    Client.versionList2.add(ver);
		    		mainFlag=1;
		    		
				}}
            }// file ended
			if(mainFlag==1){
			    Client.versionList.clear();
			    Client.versionList.addAll(Client.versionList2);
	    		PrintWriter writer = new PrintWriter("tmp.txt", "UTF-8");
			
			for(int i=0;i<Client.versionList2.size();i++)
			{
				writer.println(Client.versionList2.get(i).getFileName()+" "+
					Client.versionList2.get(i).getAuthor()+" "+
			 		 Client.versionList2.get(i).getTime()+" "+
			  		  Client.versionList2.get(i).getVersion()+" "+
			   		  Client.versionList2.get(i).getTTR()+" "+
 						Client.versionList2.get(i).getValid()+" "+
                                                Client.versionList2.get(i).getIpAddr()+" "+
						Client.versionList2.get(i).getPort());

					  } 
			writer.close();
	Process p = Runtime.getRuntime().exec("mv tmp.txt fileVersion.txt");
	System.out.println("mv tmp.txt fileVersion.txt");
		}
     	} // try 
        catch(FileNotFoundException ex) {
        }
 	catch(Exception e)
        {
	     System.out.println(e.getMessage());
	}
    }
public void invalidateSlaves(String fileName)
{

	 Query query = new Query();
         query.setFileName(fileName);
         query.setIpAddr(Client.ClientIP);
         query.setPort(Client.ClientPort);
         query.setQueryType(QueryType.Invalid);
                for(int i=0;i<Client.peers.size();i++){
                try
                {
                        {
                                Socket socket=new Socket(Client.peers.get(i).getIpAddr(),Client.peers.get(i). getPort());
                                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                oos.writeObject(query);
                                socket.close();
                        }
                }
                catch(Exception e)
                {
                //        System.out.println(e.getMessage());
                }
                }
}

}
