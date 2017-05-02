import java.io.*;
import java.net.*;

public class IPv4Client {
    public static void main(String[] args) throws UnknownHostException, IOException {
        try (Socket socket = new Socket("codebank.xyz", 38003)) {
            OutputStream out = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            InetAddress address = socket.getInetAddress();
            byte[] dstAdr = address.getAddress();
            int data = 1;
            int len = 20;
            for (int i = 0; i < 12; i++) {
                data *= 2;
                int size = len + data;
                byte[] packet = new byte[size];
                packet[0] = 0x45; //Version and Header Length
                packet[1] = 0; //Type of Service
                packet[2] = (byte) (size >>> 8); //Length
                packet[3] = (byte) size; //Length
                packet[4] = 0; //Identification
                packet[5] = 0; //Identification
                packet[6] = 0x40; //Flags
                packet[7] = 0; //Offset
                packet[8] = 50; //Time To Live
                packet[9] = 6; //Protocol
                packet[12] = 0x00; //Source Address
                packet[13] = 0x00; //Source Address
                packet[14] = 0x00; //Source Address
                packet[15] = 0x00; //Source Address
                packet[16] = dstAdr[0]; //Destination Address
                packet[17] = dstAdr[1]; //Destination Address
                packet[18] = dstAdr[2]; //Destination Address
                packet[19] = dstAdr[3]; //Destination Address
                short check = checksum(packet);
                packet[10] = (byte) (check >>> 8); //Check Sum
                packet[11] = (byte) check; //Check Sum
                out.write(packet); // Send to Server
                System.out.println("Data Length: " + data);
                System.out.println(br.readLine()); // Print Response			
            }
        }

    }

    public static short checksum(byte[] b) {
        int l = b.length;
        int i = 0;
        long total = 0;
        long sum = 0;

        while (l > 1) {
            sum = sum + ((b[i] << 8 & 0xFF00) | ((b[i + 1]) & 0x00FF));
            i = i + 2;
            l = l - 2;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum++;
            }
        }
        if (l > 0) {
            sum += b[i] << 8 & 0xFF00;
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum++;
            }
        }
        total = (~((sum & 0xFFFF) + (sum >> 16))) & 0xFFFF;
        return (short) total;
    }

}