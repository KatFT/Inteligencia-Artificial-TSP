import java.util.Scanner;
import java.awt.geom.Point2D; //para as coordenadas
import java.util.Random; //para criar pontos randoms
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


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
		int i=0;
		Random seed= new Random();
		while(i<n){
			//int number = random.nextInt(max - min) + min;
			x= (double)seed.nextInt(2*m) -m;
			y=(double)seed.nextInt(2*m) -m;
			if(!verificarPontos(x,y)){ // vai verificar se existe pontos repetidos
				arrayC[tamanho++]= new Point2D.Double(x,y);
				i++;
			}
		}
	}

	boolean verificarPontos(double x, double y){
		if(this.tamanho==0)
			return false;
		for(int i=0;i<this.tamanho;i++){
			if(x==arrayC[i].getX() && y==arrayC[i].getY())
				return true;
		}
		return false;
	}

	void printArrayPontos(){
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)arrayC[i].getX() + ","+(int)arrayC[i].getY()+")");
		}
		System.out.println();
	}
	void printArrayMatriz(){
		for(int i=0;i<this.tamanho;i++){
			for(int j=0;j<this.tamanho;j++){
				if(this.adj[i][j])
					System.out.print(" X ");
				else	
				System.out.print(" O ");
			}
			System.out.println();
		}
	}

	void clear(){
		for(int i=0; i<this.tamanho;i++){
			this.visitados[i]=false;
			for(int j=0;j<this.tamanho;j++){
				this.adj[i][j]=false;
			}
		}
	}
	//este funciona porem pode criar ciclos dentro do grafo
	/*void permutation(){
		int x;
		for(int i=0;i<this.tamanho;i++){
			Random number= new Random();
			do{
				x= number.nextInt(this.tamanho);
			}while(x==i || this.visitados[x]);
			adj[i][x]=adj[x][i]=true;
			visitados[x]=true;
			System.out.print("("+i+","+x+")");
			//System.out.print("("+(int) arrayC[x].getX()+","+(int) arrayC[x].getY()+")");
			//System.out.println(" -> ("+(int) arrayC[i].getX()+","+(int) arrayC[i].getY()+")");
		}
		System.out.println();
	}*/

	void permutation(){
		Random number= new Random();
		for(int i=1;i<this.tamanho;i++){
			int swap= number.nextInt(this.tamanho);
			Point2D tmp= arrayC[swap];
			arrayC[swap]=arrayC[i];
			arrayC[i]=tmp;
			adj[i][i-1]=adj[i-1][i]=true;
			//System.out.print("("+i+","+(i-1)+")");
			//System.out.print("("+(int) arrayC[i-1].getX()+","+(int) arrayC[i-1].getY()+")");
			//System.out.println(" -> ("+(int) arrayC[i].getX()+","+(int) arrayC[i].getY()+")");
		}
		//System.out.print("("+0+","+(this.tamanho-1)+")");

		adj[this.tamanho-1][0]=adj[0][this.tamanho-1];

		System.out.println();

	}


	void nnf(){
		int x;
		Random  number= new Random();
		x=number.nextInt(this.tamanho);
		Point2D temp=new Point2D.Double();
		int indicemin=0;
		if(x!=0){
			temp=arrayC[0];
			arrayC[0]=arrayC[x];
			arrayC[x]=temp;	
		}	
		for(int j=1; j<this.tamanho;j++){
			double min=arrayC[0].distanceSq(arrayC[j]);
			int i=0;
			indicemin=j;
			while(i<this.tamanho){
				if(arrayC[0].distanceSq(arrayC[i]) < min && j!=i){
					min=arrayC[0].distanceSq(arrayC[i]);
					indicemin=i;
				}
				i++;
			}
			if(indicemin != j+1){
				Point2D a = arrayC[indicemin]; //minimo, passar para o lado esquerdo
				arrayC[indicemin]=arrayC[j];
				arrayC[j]=a;
			}

			adj[j][j-1]=adj[j-1][j]=true;

		}
		adj[0][this.tamanho-1]=adj[this.tamanho-1][0]=true;
	}

	//verifica se os segmentos se itersetam
	boolean intersecao(Point2D a, Point2D b, Point2D c, Point2D d) {
	    double det = (b.getX() - a.getX()) * (d.getY() - c.getY()) - (d.getX() - c.getX()) * (b.getY() - a.getY());
	    if (det == 0)
	        return false; //Lines are parallel
	    double lambda = ((d.getY() - c.getY()) * (d.getX() - a.getX()) + (c.getX() - d.getX()) * (d.getY() - a.getY())) / det;
	    double gamma = ((a.getY() - b.getY()) * (d.getX()- a.getX()) + (b.getX() - a.getX()) * (d.getY() - a.getY())) / det;
	    return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
	}

	void exchange(){
		printArrayPontos();

		for(int i=1;i<this.tamanho;i++){
			for(int j=1;j<this.tamanho;j++){
				if(j!=(i-1) && j!=i && (j-1)!=i && (j-1)!=(i-1)){
					//se houver interseção, então vai haver a troca de segmentos
					if(intersecao(arrayC[i],arrayC[i-1],arrayC[j],arrayC[j-1])){
						Point2D temp= arrayC[i];
						arrayC[i] = arrayC[j-1];
						arrayC[j-1]=temp;
						System.out.print("("+(int) arrayC[i].getX()+","+(int) arrayC[i].getY()+")");
						System.out.println(" -> ("+(int) arrayC[j-1].getX()+","+(int) arrayC[j-1].getY()+")");
					}

				}
			}
		}
	}
}



public class RPG{
	public static void main(String[] args){
		Scanner ler= new Scanner(System.in);
		System.out.println("Quantidade de pontos no plano:");
		int n= ler.nextInt();
		System.out.println("Insira o range desejado:");
		int m= ler.nextInt();
		Grafo garf= new Grafo(n); //criamos o nosso grafo de pontos de tamanho n

		garf.criacaoPontos(n,m); //Gerar aleatoriamentende pontos no plano com coordenadas inteiras, de −m a m, para n e m dados.
		System.out.print("Array de pontos original:");
		garf.printArrayPontos();

		System.out.println("Escolha uma das seguintes alternativas para criar ligações:");
		System.out.println("1-Gerar uma permutação qualquer dos pontos.");
		System.out.println("2-Heurıstica'nearest-neighbour first'");
		int ex= ler.nextInt();
		switch(ex){
			case 1: garf.permutation();
					garf.printArrayPontos();
				    break;
			case 2: garf.nnf();
					garf.printArrayPontos();
					break;
		}
		System.out.println("1-Determinar vizinhaça obtida por 2-exchange");
		ex= ler.nextInt();
		if(ex==1){
			garf.exchange();
			garf.printArrayPontos();
		}
	}
}