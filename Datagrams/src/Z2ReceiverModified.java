import java.net.*;
import java.util.HashMap;
import java.util.Map;

/*

	This file is modified by me.
	Modified to use threads to receive the message and printer.
	It's as delivered by lecturer.

 */

public class Z2ReceiverModified
{
    static HashMap<Integer, Character> received = new HashMap<>();
    static final int endOfTransmissionCode = -32767;
    static final int datagramSize=50;
    InetAddress localHost;
    int destinationPort;
    DatagramSocket socket;

    ReceiverThread receiver;

    public Z2ReceiverModified(int myPort, int destPort)
            throws Exception
    {
        localHost=InetAddress.getByName("127.0.0.1");
        destinationPort=destPort;
        socket=new DatagramSocket(myPort);
        receiver=new ReceiverThread();
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

                    // Check if the packet is EoT code
                    if (p.getIntAt(0) == endOfTransmissionCode) {
                        System.out.print("[Message Received]\n");
                        for (Map.Entry<Integer, Character> e : received.entrySet())
                        {
                            System.out.print(e.getValue());
                        }
                        System.out.println();
                        break;
                    }

                    // Check if new num with given number sequence
                    if (!received.containsKey(p.getIntAt(0))) {
                        received.put(p.getIntAt(0), (char) p.data[4]);
                        messageTillSequence(p.getIntAt(0));
                    }


                    // WYSLANIE POTWIERDZENIA
					//System.out.println("R:"+p.getIntAt(0)
                    //        +": "+(char) p.data[4]);
                    packet.setPort(destinationPort);
                    socket.send(packet);
                }
            }
            catch(Exception e)
            {
                System.out.println("Z2Receiver.ReceiverThread.run: "+e);
            }
        }

    }

	// Printer of current gathered text about the message as send
    private void messageTillSequence(int index) {

        System.out.print("[Assembled]\n");
        for (int i = 0; i < index; i++)
        {
            if (!received.containsKey(i))
            {
                System.out.print("�");
            }
            else
            {
                System.out.print(received.get(i).charValue());
            }
        }
        System.out.print("\n\n");

    }


    public static void main(String[] args)
            throws Exception
    {
        Z2ReceiverModified receiver=new Z2ReceiverModified( Integer.parseInt(args[0]),
                Integer.parseInt(args[1]));
        receiver.receiver.start();
    }


}