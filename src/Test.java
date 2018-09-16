import java.io.*;
import java.net.*;
import java.util.*;

public class Test {

	private ServerSocket ss;
	int vbo;

	public static void main(String[] args) {
		System.out.println("Saisissez un mois de l'ann�e (1 - 12) :");
		Scanner in = new Scanner(System.in);
		int m = in.nextInt();
		long j = (new Date().getTime()) / 1000 / 86400;
		long a = (1970+(j-(j+730)/1461)/365);
		int[] t = {31, a%4==0 ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
		int s = 0;
		j -= 365*(a-1970)+(a-1969)/4-1;
		for(int i = 0; i < t.length; i++)
			if(j <= (s+=t[i])) {
				System.out.println((i+1==m) ? "nous nous trouvons dans ce mois" : "nous ne sommes pas dans ce mois");
				break;
			}
		in.close();	
	}

	public Test() {
		try {
			ss = new ServerSocket(4498);
			while(true) {
				Socket socket = ss.accept();
				Thread t = new Thread(new Channel(socket));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class Channel implements Runnable {
	
		private static final String PATH = "C:/Users/Yoann/Documents/Programmation/site web";
	
		private Socket socket;
		private BufferedReader in;
		private DataOutputStream out;
		
		public Channel(Socket socket) {
			try {
				this.socket = socket;
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run() {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			try {
				String fl = in.readLine();
				System.out.println(fl);
				StringTokenizer tokenizer = new StringTokenizer(fl);
				tokenizer.nextToken();
				String request = tokenizer.nextToken();
				request = URLDecoder.decode(request, "UTF-8");
				while(in.ready()) {
//					in.readLine();
					System.out.println(in.readLine());
				}
				if(request.equals("/src.rar")) {
					out.writeBytes("HTTP/1.1 403 FORBIDDEN\n");
					out.writeBytes("Server: Server Java de Yoann Coudert\n");
				} else {
					String fn = PATH + request;
					FileInputStream file = new FileInputStream(fn);
					out.writeBytes("HTTP/1.1 200 OK\n");
					out.writeBytes("Server: Server Java de Yoann Coudert\n");
					if(request.endsWith("html"))
						out.writeBytes("Content-Type: text/html\n");
					else if(request.endsWith("css"))
						out.writeBytes("Content-Type: text/css\n");
					else if(request.endsWith("jpg"))
						out.writeBytes("Content-Type: image/jpg\n");
					else if(request.endsWith("png"))
						out.writeBytes("Content-Type: image/png\n");
					out.writeBytes("\r\n");
					sendFile(file, out);
				}
				System.out.println("DONE");
			} catch (IOException e) {
				try {
					out.writeBytes("HTTP/1.1 404 NOT FOUND\n");
					out.writeBytes("Server: Server Java de Yoann Coudert\n");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				try {
					out.flush();
					in.close();
					out.close();
					socket.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	
		private void sendFile (FileInputStream fin, DataOutputStream out) throws IOException {
			byte[] buffer = new byte[1024] ;
			int bytesRead;
			while ((bytesRead = fin.read(buffer)) != -1 ) {
				out.write(buffer, 0, bytesRead);
			}
			fin.close();
			System.out.println("FILE SEND");
		}
	
	}

}