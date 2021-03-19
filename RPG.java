import java.util.Scanner;
import java.awt.geom.Point2D; //para as coordenadas
import java.util.Random; //para criar pontos randoms




class Grafo{
	int tamanho; // numero de nos no grafo
	boolean adj[][]; // matriz de adjacencias
	boolean visitados[]; //matriz q verifica visitados
	Point2D arrayC[]; //array aonde vao ficar as coordenadas
	

	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
		this.adj= new boolean[tamanho][tamanho];
		this.visitados= new boolean[tamanho];
	}

	//funçao geradora de pontos (Random)
	void criacaoPontos(int n,int m){
		double x,y;
		
		Random seed= new Random();
		for(int i=0;i<n;i++){
			//int number = random.nextInt(max - min) + min;
			x= (double)seed.nextInt(2*m) -m;
			y=(double)seed.nextInt(2*m) -m;
			arrayC[tamanho++]= new Point2D.Double(x,y);
		}
	}

	void printPontos(){
		for(int i=0;i<this.tamanho;i++){
			System.out.println("("+arrayC[i].getX() + ","+arrayC[i].getY()+")");
		}
	}

	void permutation(){
		int x;
		for(int i=0;i<this.tamanho;i++){
			Random number= new Random();
			do{
				x= number.nextInt(this.tamanho);
			}while(x==i || this.visitados[x]);
			adj[i][x]=adj[x][i]=true;
			visitados[x]=true;
			System.out.print("("+arrayC[x].getX()+","+arrayC[x].getY()+")");
			System.out.println(" -> ("+arrayC[i].getX()+","+arrayC[i].getY()+")");
		}
	}

	void nnf(){
		int x;
		Random  number= new Random();
		x=number.nextInt(this.tamanho);
		Point2D temp=new Point2D.Double();
		if(x!=0){
			temp=arrayC[0];
			arrayC[0]=arrayC[x];
			arrayC[x]=temp;	
		}	
		for(int j=0; j<this.tamanho;j++){

			double min=arrayC[0].distanceSq(arrayC[j]);
			for(int i=j+1;i<this.tamanho;i++){
				if(arrayC[0].distanceSq(arrayC[i])<min){
					min=arrayC[0].distanceSq(arrayC[i]);
					temp=arrayC[i];

				}
				Point2D a = temp;
				temp=arrayC[i];
				arrayC[i]=a;
				adj[i][j]=adj[j][i]=true;
			System.out.print("("+arrayC[j].getX()+","+arrayC[j].getY()+")");
				
			System.out.println(" -> ("+arrayC[i].getX()+","+arrayC[i].getY()+")");

			}
				visitados[j]=true;

			
		}

			
		
	}
}



public class RPG{
	public static void main(String[] args){
		Scanner ler= new Scanner(System.in);
		//ler nº de pontos no plano
		System.out.println("Quantidade de pontos no plano:");
		int n= ler.nextInt();
		//range para ser gerado os pontos
		System.out.println("Insira o range desejado:");
		int m= ler.nextInt();

		Grafo garf= new Grafo(n); //criamos o nosso grafo de pontos de tamanho n

		//cria as coordenadas aleatorias
		garf.criacaoPontos(n,m);
		
		//imprime os nossos pontos
		System.out.println("Print dos pontos por ordem:");
		garf.printPontos();
		System.out.println("------------------");
		System.out.println("Print dos pontos permutados:");
		//permutaçao dos pontos
		garf.permutation();
		System.out.println("------------------");
		garf.nnf();

	}
}