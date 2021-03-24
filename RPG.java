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

	//pega na nossa reta e transforma-a para um array de pontos
	Point2D[] toarray(){
		return x;
	}
}

class Grafo{
	int tamanho; // numero de nos no grafo
	Point2D[] arrayC; //array aonde vao ficar as coordenadas
	Point2D[] best_so_far; //usado no hill climbing para determinar o melhor
	LinkedList<Reta> lista= new LinkedList<>();  
	
	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
		this.best_so_far= new Point2D[tamanho];

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

//vai ser utilizado para o hill cimbling
	void hill_exchange(){
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
					if(intersecao(this.best_so_far[i],this.best_so_far[i-1],this.best_so_far[b],this.best_so_far[a])){
						//Imprime as trocas
						System.out.print("("+(int)this.best_so_far[i].getX()+","+(int)this.best_so_far[i].getY()+")");
						System.out.println("->("+(int) this.best_so_far[a].getX()+","+(int) this.best_so_far[a].getY()+")");
	
						if(a<i)	
							novoarray=reverse(a,i,this.best_so_far.clone());
						else 
							novoarray=reverse(i,a,this.best_so_far.clone());

						lista.addLast(new Reta(novoarray));
						//System.out.print("Array"+ lista.size()+": ");
						//printArrayPontos();
						printLista();
					}
				}
			}
		}

	}

	
	Point2D[] opcao(int op){
		//Point2D[] teste;
		switch(op){
			case 1: //perimetro
					int i=0;
					double j=Double.MAX_VALUE;
					int pos=0; //guarda posiçao
					for(Reta res : lista){ //para cada posiçao da lista
						double calc=perimetro(res.toarray());//calculamos o perimetro
						if(calc<j){//se for menor, troca e muda a posiçao para computar a metrica
							j=calc;
							pos=i;//computa a metrica
						}
						i++;//senao avança
					}
					return lista.remove(pos).toarray(); //depois retiramos na lista para ser avaliado

					/*Point2D[] res= lista.removeFirst().toarray();
					double min= perimetro(res); 
					for(int i=1;i<lista.size();i++){
						double m= perimetro(lista.remove(i).toarray());
					
						if(m<min){
							min=m;
							res=lista.remove(i).toarray();
						}
					}
					return res;*/
					
			case 2: //retiramos o primeiro da lista para ser avaliado
					return lista.removeFirst().toarray();
					
			case 3: //retira o elemento da lista com menos conflitos

					int k=0;
					double min= Double.MAX_VALUE;
					int posicao=0;
					int minimo=0;
					for(Reta res : lista){
						Point2D[] b= res.toarray();
						minimo=inter(b);
						if(minimo<min){
							min=minimo;
							posicao=k;
						}
						k++;
					}
					return lista.remove(posicao).toarray();
					
			case 4: //retiramos um random da lista para ser avaliado
					Random s= new Random();
					return lista.remove(s.nextInt(lista.size())).toarray();
			default: return null;
					
		}

	}

	//funçao para saber o nº interseçoes (utilizado no 4 c))
	int inter(Point2D[] array){
		int count=0;
		for(int i=1;i<this.tamanho;i++){
			for(int j=1;j<this.tamanho;j++){
				
				if(intersecao(array[i-1],array[i],array[j-1],array[j]))
					count++;
				
			}
		}
		return count;
	}

	//Calcula o perimetro do poligono
	double perimetro(Point2D[] array){
		double soma=0;
		for(int i=1;i<this.tamanho;i++){
			soma+=array[i-1].distanceSq(array[i]);
		}
		soma+=array[0].distanceSq(array[this.tamanho-1]);
		return soma;
	}

	//imprime a solução final
	void arrayFinal(Point2D[] a,double res){
		System.out.print("\nArray Solução: ");
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)a[i].getX() + ","+(int)a[i].getY()+")");
		}
		System.out.println();
		System.out.println("Perimetro: "+res);
		System.out.println();
	}

	//algoritmo para calcular o array aonde o perimetro e minimo
	void hillClimbing(int op){

		this.best_so_far=arrayC; //estado inicial
		hill_exchange();
		double res=0.0;
		//imprimir array
		while(!lista.isEmpty()){
			System.out.println("Iteraçao Passada");

			Point2D[] candidate= opcao(op); //candidato 
			double min=perimetro(this.best_so_far);
			double max=perimetro(candidate);
			if(max<min){
				max=min;
				res=max;
				best_so_far=candidate;
				hill_exchange();
			}	
			res=min;	
		}
		arrayFinal(best_so_far,res);
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
					System.out.println("Minimo Perímetro: ");

					garf.hillClimbing(1);
					//garf.printArrayPontos();
				    break;
			case 2: 
					garf.printArrayPontos();
					garf.hillClimbing(2);			
					//garf.printArrayPontos();
					break;
			case 3: 
								
					garf.printArrayPontos();
					garf.hillClimbing(3);
					break;
			case 4: 
								
					garf.printArrayPontos();
					garf.hillClimbing(4);
					break;
		}
		return garf;
	}
}