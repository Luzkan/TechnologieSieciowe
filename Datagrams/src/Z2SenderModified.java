import java.net.*;
import java.util.HashMap;

/*

	This file is modified by me.
	Modified to synchronized threads to deliver message.
	It's as delivered by lecturer.

 */

class Z2SenderModified
{
    static final HashMap<Integer, Thread> delivery = new HashMap<>();
    static final int endOfTransmissionCode = -32767;
    static final int datagramSize=50;
    static final int sleepTime=500;
    static final int maxPacket=50;
    InetAddress localHost;
    int destinationPort;
    DatagramSocket socket;
    SenderThread sender;
    ReceiverThread receiver;


    public Z2SenderModified(int myPort, int destPort)
            throws Exception
    {
        localHost=InetAddress.getByName("127.0.0.1");
        destinationPort=destPort;
        socket=new DatagramSocket(myPort);
        sender=new SenderThread();
        receiver=new ReceiverThread();
    }

    // Thread with number sequence and packet data
    private Thread deliverer(int id, int data) {
        return new Thread()
        {
            synchronized public void run()
            {
                try
                {
                    wait(sleepTime);
                }
                catch (InterruptedException e)
                {
                    synchronized (delivery)
                    {
                        // Check if everything was delivered to the Receiver
                        // If true, then it can travel to receiver without problems.
                        if (delivery.isEmpty())
                        {
                            delivery.put(endOfTransmissionCode, deliverer(endOfTransmissionCode, 0));
                            delivery.get(endOfTransmissionCode).start();
                        }
                    }
                    return;
                }

                Z2Packet p = new Z2Packet(4 + 1);
                p.setIntAt(id, 0);
                p.data[4] = (byte) data;
                DatagramPacket packet =
                        new DatagramPacket(p.data, p.data.length,
                                localHost, destinationPort);

                // Resend
                try
                {
                    synchronized (socket)
                    {
                        socket.send(packet);
                    }
                    synchronized (delivery)
                    {
                        delivery.remove(id);
                        delivery.put(id, deliverer(id, data));
                        delivery.get(id).start();
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Z2Sender.SenderThread.run: "+e);
                }
            }
        };
    }

    class SenderThread extends Thread
    {
        public void run()
        {
            int i, x;
            try
            {
                for(i=0; (x=System.in.read()) >= 0 ; i++)
                {
                    Z2Packet p=new Z2Packet(4+1);
                    p.setIntAt(i,0);
                    p.data[4]= (byte) x;
                    DatagramPacket packet =
                            new DatagramPacket(p.data, p.data.length,
                                    localHost, destinationPort);
                    socket.send(packet);

                    synchronized (delivery)
                    {
                        // Map ID of the char to a Thread (transmission manager) with number sequence and packet data
                        delivery.put(i, deliverer(i, x));
                        delivery.get(i).start();
                    }

                    sleep(sleepTime);
                }
            }
            catch(Exception e)
            {
                System.out.println("Z2Sender.SenderThread.run: "+e);
            }
        }

    }

    class ReceiverThread extends Thread
    {
        public void run()
        {
            try
            {
                while(true)
                {
                    byte[] data=new byte[datagramSize];
                    DatagramPacket packet=
                            new DatagramPacket(data, datagramSize);
                    socket.receive(packet);
                    Z2Packet p=new Z2Packet(packet.getData());

                    synchronized (delivery) {
                        // If successfully delivered (has data from that packet)
                        if (delivery.containsKey(p.getIntAt(0)))
                        {
                            // Terminate the thread for that character
                            // Interruption activates endOfTransmissionCode
                            delivery.get(p.getIntAt(0)).interrupt();
                            delivery.remove(p.getIntAt(0));

                            // Inform which char was transmitted
                            System.out.println("S:"+p.getIntAt(0)+
                                    ": "+(char) p.data[4]);
                        }
                    }


                }
            }
            catch(Exception e)
            {
                System.out.println("Z2Sender.ReceiverThread.run: "+e);
            }
        }

    }


    public static void main(String[] args)
            throws Exception
    {
        Z2SenderModified sender=new Z2SenderModified( Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        sender.sender.start();
        sender.receiver.start();
    }



}