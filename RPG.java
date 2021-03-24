import java.util.Scanner;
import java.awt.geom.Point2D; //para as coordenadas
import java.util.Random; //para criar pontos randoms
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;

class Reta{
	public Point2D[] x;

	Reta(Point2D[] a){
		x=a.clone();
	}
}

class Grafo{
	int tamanho; // numero de nos no grafo
	Point2D[] arrayC; //array aonde vao ficar as coordenadas
	LinkedList<Reta> lista= new LinkedList<>();  
	
	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
	}

	//Ex1-Funçao geradora de pontos (Random)
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

	//Ex1-Verificação se os pontos já existem
	boolean verificarPontos(double x, double y){
		if(this.tamanho==0) return false;
		for(int i=0;i<this.tamanho;i++){
			if(x==arrayC[i].getX() && y==arrayC[i].getY())
				return true;
		}
		return false;
	}

	//Ex2.1  Permutação de pontos
	void permutation(){
		Random number= new Random();
		for(int i=1;i<this.tamanho;i++){
			int swap= number.nextInt(this.tamanho);
			Point2D tmp= arrayC[swap];
			arrayC[swap]=arrayC[i];
			arrayC[i]=tmp;
		}
	}

	//Ex2.2 Nearest-neighbour first
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
		}
	}

	//Verifica se os segmentos se itersetam
	boolean intersecao(Point2D a, Point2D b, Point2D c, Point2D d) {
	    double det = (b.getX() - a.getX()) * (d.getY() - c.getY()) - (d.getX() - c.getX()) * (b.getY() - a.getY());
	    if (det == 0)
	        return false; //Lines are parallel
	    double lambda = ((d.getY() - c.getY()) * (d.getX() - a.getX()) + (c.getX() - d.getX()) * (d.getY() - a.getY())) / det;
	    double gamma = ((a.getY() - b.getY()) * (d.getX()- a.getX()) + (b.getX() - a.getX()) * (d.getY() - a.getY())) / det;
	    return (0 < lambda && lambda < 1) && (0 < gamma && gamma < 1);
	}

	//Reverte o restante array depois do exchange
	Point2D[] reverse(int i,int a, Point2D[] novoarray){
		for(int j=i;j<a;j++){
			Point2D temp= novoarray[j];
			novoarray[j]=novoarray[a];
			novoarray[a]=temp;
			a--;
		}
		return novoarray;
	}

	//Ex3 Determinar a vizinhança obtida por (2-exchange)
	void exchange(){
		Point2D[] novoarray;
		int a=0,b=0;
		for(int i=1;i<this.tamanho;i++){
			for(int j=i;j<=this.tamanho;j++){
				if(j==this.tamanho){
					 a=0;
					 b=this.tamanho-1;
				}
				else{
				 a=j-1;
				 b=j;
				}
				if(b!=(i-1) && b!=i && a!=i && a!=(i-1)){
					//se houver interseção, então vai haver a troca de segmentos
					if(intersecao(arrayC[i],arrayC[i-1],arrayC[b],arrayC[a])){
						//Imprime as trocas
						System.out.print("("+(int)arrayC[i].getX()+","+(int)arrayC[i].getY()+")");
						System.out.println("->("+(int) arrayC[a].getX()+","+(int) arrayC[a].getY()+")");
	
						if(a<i)	
							novoarray=reverse(a,i,arrayC.clone());
						else 
							novoarray=reverse(i,a,arrayC.clone());

						lista.addLast(new Reta(novoarray));
						//System.out.print("Array"+ lista.size()+": ");
						//printArrayPontos();
						printLista();
					}
				}
			}
		}
		
	}

	//Calcula o perimetro do poligono
	double perimetro(){
		double soma=0;
		for(int i=1;i<this.tamanho;i++){
			soma+=arrayC[i-1].distanceSq(arrayC[i]);
		}
		soma+=arrayC[0].distanceSq(arrayC[this.tamanho-1]);
		System.out.println("SOMA: "+soma);
		return soma;
	}

	void comparar(){
		hillClimbing();
		exchange();
		double soma=perimetro();
		double min=0;
		do{
		permutation();
		min=perimetro();
		}while(soma>min);
		System.out.println("Resultado final: "+min);
	}
	

	void hillClimbing(){
		int index=0;

		double min=Double.MAX_VALUE;
		for(int i=1;i<this.tamanho;i++){
			Point2D current= arrayC[i-1];
			for(int j=1;j<this.tamanho;j++){

				double d1=current.distanceSq(arrayC[j]);
				if(d1<min){
					min=d1;
					index=j;
				}
			}
			Point2D temp= arrayC[i];
			arrayC[i] = arrayC[index];
			arrayC[index]=temp;
		}

	}

	void hillClimbinga(){
		int index=0;
		double min=Double.MAX_VALUE;
		for(int i=1;i<this.tamanho;i++){
			Point2D current= arrayC[i-1];
			for(int j=1;j<this.tamanho;j++){
				double d1=current.distanceSq(arrayC[j]);
				if(d1<min){
					min=d1;
					index=j;
				}
			}
			Point2D temp= arrayC[i];
			arrayC[i] = arrayC[index];
			arrayC[index]=temp;
		}

	}
	//Imprime os pontos do array
	void printArrayPontos(){
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)arrayC[i].getX() + ","+(int)arrayC[i].getY()+")");
		}
		System.out.println();
	}
	void printLista(){
		for(int i=0;i<this.lista.size();i++){
			for(int j=0; j<this.tamanho;j++)
				System.out.print("("+(int)this.lista.get(i).x[j].getX()+","+(int) this.lista.get(i).x[j].getY()+")  ");

			System.out.println();
		}
		System.out.println();
	}


}


public class RPG{  
	public static void main(String[] args){
		Scanner ler= new Scanner(System.in);
		Grafo garf=new Grafo(0);
		int opcao=0;
		boolean inicio=false;

		do{
			clearScreen();
			MenuExercicios();
			opcao=ler.nextInt();
			clearScreen();
			if(opcao!=1 && inicio==false){
				inicio=true;
				System.out.println("Para fazer o exercicio, temos que criar o array de pontos!\n");
				garf=Ex1(garf);
			}

			switch(opcao){
				case 1: garf= Ex1(garf);
						inicio=true;
						break;
				case 2: garf=Ex2(garf);
						break;
				case 3: garf=Ex3(garf);
						break;
				case 4: garf=Ex4(garf);
						break;
				case 5: 
						break;
				case 6: 
						break;
			}
			System.out.print("\n\n0(Sair) / Outro Número (Continuar)   ");
			opcao=ler.nextInt();
		}while(opcao!=0);
	}

	public static void clearScreen() {  
		System.out.print("\033[H\033[2J");  
		System.out.flush();  
	}

	public static void MenuExercicios(){
		System.out.println("Trabalho 1- IA\n");
		System.out.println("1 - Gerar aleatoriamente pontos no plano");
		System.out.println("2 - Determinar um candidato a solução");
		System.out.println("3 - Determinar a vizinhança obtida por (2-exchange)");
		System.out.println("4 - Aplicar melhoramento iterativo (hill climbing)");
		System.out.println("5 - Aplicar simulated annealing");
		System.out.println("6 - Aplicar metaheurística ACO (ant colony optimization)");
		System.out.println("Escolha o exercicio:");
	}
	
	public static Grafo Ex1(Grafo garf){
		Scanner ler= new Scanner(System.in);
		System.out.println("Ex1:\n");
		System.out.print("Quantidade de pontos no plano: ");
		int n= ler.nextInt();

		System.out.print("Insira o range desejado: ");
		int m= ler.nextInt();
		garf= new Grafo(n); 
		garf.criacaoPontos(n,m);

		System.out.print("Novo Array de pontos: ");
		garf.printArrayPontos();
		return garf;
	}

	public static Grafo Ex2(Grafo garf){
		Scanner ler= new Scanner(System.in);
		int opcao=0;
		System.out.println("\n Ex2:\n ");
		System.out.println("Escolha uma das seguintes alternativas para criar ligações:");
		System.out.println("1-Gerar uma permutação qualquer dos pontos.");
		System.out.println("2-Heuristica 'nearest-neighbour first'");
		opcao=ler.nextInt();

		System.out.print("      Array de Original: ");
		garf.printArrayPontos();

		switch(opcao){
			case 1: System.out.print("   Permutação de pontos: ");
					garf.permutation();
					garf.printArrayPontos();
				    break;
			case 2: System.out.print("Nearest-neighbour first: ");
					garf.nnf();
					garf.printArrayPontos();
					break;
		}
		return garf;
	}

	public static Grafo Ex3(Grafo garf){
		System.out.println("Ex3:\n");
		System.out.print("       Array de Original: ");
		garf.printArrayPontos();
		System.out.println("    Vizinhaça 2-exchange:  ");
		garf.exchange();
		//garf.printArrayPontos();
		return garf;
	}

	public static Grafo Ex4(Grafo garf){
		Scanner ler= new Scanner(System.in);
		System.out.println("Ex4:\n");
		System.out.println("Escolha uma das seguintes alternativas para escolher o candidato na vizinhança “2-exchange”:");
		System.out.println("1-Minimo Perímetro - 'best-improvement first'");
		System.out.println("2-Primeiro candidato nessa vizinhança - 'first-improvement'");
		System.out.println("3-Menos Conflitos de arestas - menos cruzamentos de arestas");
		System.out.println("4-Qualquer candidato nessa vizinhaça");
		int opcao= ler.nextInt();
		switch(opcao){
			case 1: 
					garf.printArrayPontos();
					System.out.println("       Minimo Perímetro: ");
					garf.comparar();
					garf.printArrayPontos();
				    break;
			/*case 2: 
								
					garf.printArrayPontos();
					break;
			case 3: 
								
					garf.printArrayPontos();
					break;
			case 4: 
								
					garf.printArrayPontos();
					break;*/
		}
		return garf;
	}
}