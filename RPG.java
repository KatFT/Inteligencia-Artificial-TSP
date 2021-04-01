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
	LinkedList<Reta> lista;  
	
	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
		this.best_so_far= new Point2D[tamanho];
		this.lista= new LinkedList<>();

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
		Random number= new Random(); x=number.nextInt(this.tamanho);
		Point2D temp=new Point2D.Double(); //Para ajudar na troca de posiçoes

		int indicemin=0;
		//Fazer a troca da posição inicial
		if(x!=0){
			temp=arrayC[0];
			arrayC[0]=arrayC[x];
			arrayC[x]=temp;	
		}	
		//System.out.println("Trocou 1pos");
		//printArrayPontos();
		for(int j=0; j<this.tamanho-1;j++){
			int i=j+1;
			double min=arrayC[i].distanceSq(arrayC[j]);
			indicemin=i;
			i++;
			while(i<this.tamanho){
				if(arrayC[j].distanceSq(arrayC[i]) < min && j!=i){
					min=arrayC[j].distanceSq(arrayC[i]);
					indicemin=i;
				}
				i++;
			}
			if(indicemin != j){
				Point2D a = arrayC[indicemin]; //minimo, passar para o lado esquerdo
				arrayC[indicemin]=arrayC[j+1];
				arrayC[j+1]=a;
			}
		}
	}

	//Ex3 Determinar a vizinhança obtida por (2-exchange)
	void exchange(){
		System.out.println("Troca de pontos:");
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
						System.out.print((lista.size())+": ");
						System.out.print("("+(int)arrayC[i].getX()+","+(int)arrayC[i].getY()+")");
						System.out.println("->("+(int) arrayC[a].getX()+","+(int) arrayC[a].getY()+")");
	
						if(a<i)	
							novoarray=reverse(a,i,arrayC.clone());
						else 
							novoarray=reverse(i,a,arrayC.clone());

						lista.addLast(new Reta(novoarray));
						//System.out.print("Array"+ lista.size()+": ");
						//printArrayPontos();
					}
				}
			}
		}
		System.out.println();
		System.out.println("Novo array resultante das trocas anteriores:");
		printLista();
	}
	
	//https://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
	

	// Given three colinear points p, q, r, the function checks if
	// point q lies on line segment 'pr'
	static boolean onSegment(Point2D p, Point2D q, Point2D r){
	    if (q.getX() <= Math.max(p.getX(), r.getX()) && q.getX() >= Math.min(p.getX(), r.getX()) &&
	        q.getY() <= Math.max(p.getY(), r.getY()) && q.getY() >= Math.min(p.getY(), r.getY()))
	    return true;
	  
	    return false;
	}
	  
	// To find orientation of ordered triplet (p, q, r).
	// The function returns following values
	// 0 --> p, q and r are colinear
	// 1 --> Clockwise
	// 2 --> Counterclockwise
	static double orientation(Point2D p, Point2D q, Point2D r){
	    // See https://www.geeksforgeeks.org/orientation-3-ordered-points/
	    // for details of below formula.
	    double val = ((q.getY() - p.getY()) * (r.getX() - q.getX()) -
	            (q.getX() - p.getX()) * (r.getY() - q.getY()));
	  
	    if (val == 0) return 0; // colinear
	  
	    return (val > 0)? 1: 2; // clock or counterclock wise
	}

	double produtoVet(Point2D p1, Point2D q1, Point2D p2, Point2D q2){
		Point2D x= new Point2D.Double(q1.getX()-p1.getX(),q1.getY()-p1.getY());
		Point2D y= new Point2D.Double(q2.getX()-p2.getX(),q2.getY()-p2.getY());
		return (x.getX()*y.getX()) + (x.getY()*y.getY());
	}
	
	//Ex3 e 4 Verifica se os segmentos se intersetam
	boolean intersecao(Point2D p1, Point2D q1, Point2D p2, Point2D q2) {
		// Find the four orientations needed for general and
		// special cases
	    double o1 = orientation(p1, q1, p2);
	    double o2 = orientation(p1, q1, q2);
	    double o3 = orientation(p2, q2, p1);
	    double o4 = orientation(p2, q2, q1);
	  
		if(o1!=o2 && o3!=o4)
	    	return true;

		else if((o1==0 &&o2==0 && o3==0 &&o4==0)  && produtoVet(p1,q1,p2,q2)>0)
			return true;

    	

	    // Special Cases
	    // p1, q1 and p2 are colinear and p2 lies on segment p1q1
	    else if (o1 == 0 && onSegment(p1, p2, q1)) return true;
	  
	    // p1, q1 and q2 are colinear and q2 lies on segment p1q1
	    else if (o2 == 0 && onSegment(p1, q2, q1)) return true;
	  
	    // p2, q2 and p1 are colinear and p1 lies on segment p2q2
	    else if (o3 == 0 && onSegment(p2, p1, q2)) return true;
	  
	    // p2, q2 and q1 are colinear and q1 lies on segment p2q2
	    else if (o4 == 0 && onSegment(p2, q1, q2)) return true;
	  
	    return false; // Doesn't fall in any of the above cases
	}

	//Ex3 e 4 Reverte o restante array depois do exchange
	Point2D[] reverse(int i,int a, Point2D[] novoarray){
		for(int j=i;j<a;j++){
			Point2D temp= novoarray[j];
			novoarray[j]=novoarray[a];
			novoarray[a]=temp;
			a--;
		}
		return novoarray;
	}
	

	//Ex4 Calcula o perimetro do poligono
	double perimetro(Point2D[] array){
		double soma=0;
		for(int i=1;i<this.tamanho;i++){
			soma+=array[i-1].distanceSq(array[i]);
		}
		soma+=array[0].distanceSq(array[this.tamanho-1]);
		return soma;
	}

	//Ex4 Vai ser utilizado para o hill cimbling
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
						//System.out.print("("+(int)this.best_so_far[i].getX()+","+(int)this.best_so_far[i].getY()+")");
						//System.out.println("->("+(int) this.best_so_far[a].getX()+","+(int) this.best_so_far[a].getY()+")");
	
						if(a<i)	
							novoarray=reverse(a,i,this.best_so_far.clone());
						else 
							novoarray=reverse(i,a,this.best_so_far.clone());

						lista.addLast(new Reta(novoarray));
						//System.out.print("Array"+ lista.size()+": ");
						//printArrayPontos();
						//printLista();
					}
				}
			}
		}

	}

	//Ex4 Opçoes para o hill climbing
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

	//Ex4.3 - Funçao para saber o nº interseçoes 
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

	//Ex 4 Imprime a solução final
	void arrayFinal(Point2D[] a,double res){
		System.out.print("\nArray Solução: ");
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)a[i].getX() + ","+(int)a[i].getY()+")");
		}
		System.out.println();
		System.out.println("Perimetro: "+res);
		System.out.println();
	}

	//Ex 4 Algoritmo para calcular o array aonde o perimetro e minimo
	void hillClimbing(int op){
		this.best_so_far=arrayC; //estado inicial
		hill_exchange();
		double res=0.0;
		double min=0.0;
		double max=0.0;
		//imprimir array
		while(!lista.isEmpty()){
			//System.out.println("Iteraçao Passada");
			Point2D[] candidate= opcao(op); //candidato 
			min=perimetro(this.best_so_far);
			max=perimetro(candidate);

			if(max<min){
				max=min;
				res=max;
				best_so_far=candidate;
				lista.clear();//limpamos a lista				
				hill_exchange();
			}
		
			else {
				continue;
				/*res=max;
				arrayFinal(best_so_far,res);
				return;*/
			}

		}
		System.out.println("CARALHO");
		arrayFinal(best_so_far,res);
		
	}

	double acceptanceProbability(double min, double max, double temp) {
        // If the new solution is better, accept it
        if (max < min) {
            return 1;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((min - max) / temp);
    }

	void simA(){
		this.best_so_far= arrayC;
		double temp= (double)inter(arrayC); //temperatura
		hill_exchange();
		double res=0.0;
		while(!lista.isEmpty() && temp>0){
			//System.out.println("TEMPERATURA CRLH: "+temp);
			Point2D[] candidate= opcao(4);
			double min=perimetro(this.best_so_far);
			double max=perimetro(candidate);
			//aceita
			if(acceptanceProbability(min, max, temp)> Math.random()){

				best_so_far= candidate;
				res=max;
				hill_exchange();
			}
			res=min;
			//atualizar a temperatura
			temp=(double) 0.95*temp;

		}
		arrayFinal(best_so_far,res);

	}

	//Ex4 Imprime a lista de valores
	void printLista(){
		for(int i=0;i<this.lista.size();i++){
			System.out.print(i+": ");
			for(int j=0; j<this.tamanho;j++)
				System.out.print("("+(int)this.lista.get(i).x[j].getX()+","+(int) this.lista.get(i).x[j].getY()+")  ");

			System.out.println();
		}
		System.out.println();
	}

	//Imprime os pontos do array
	void printArrayPontos(){
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)arrayC[i].getX() + ","+(int)arrayC[i].getY()+")");
		}
		System.out.println();
	}
}


public class RPG{  
	public static void main(String[] args){
		Scanner ler= new Scanner(System.in);
		Grafo garf=new Grafo(0);
		int opcao=0;
		boolean op1=false;
		do{
			MenuExercicios();
			opcao=ler.nextInt();
			clearScreen();
			//Fazer criação do array, caso nao escolha a opçao 1
			if(opcao!=1 && op1==false){
				op1=true;
				System.out.println("Para fazer o exercicio, temos que criar o array de pontos!\n");
				garf=Ex1(garf);
			}
			clearScreen();
			switch(opcao){
				case 1: garf= Ex1(garf);
						op1=true;
						break;
				case 2: garf=Ex2(garf);
						break;
				case 3: garf=Ex3(garf);
						break;
				case 4: garf=Ex4(garf);
						break;
				case 5: garf=Ex5(garf);
						break;
				//case 6: 
					//	break;
				default: System.out.println("Opção errada, tente novamente!");
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
		clearScreen();
		System.out.println("Trabalho 1- IA\n");
		System.out.println("1 - Gerar aleatoriamente pontos no plano");
		System.out.println("2 - Determinar um candidato a solução");
		System.out.println("3 - Determinar a vizinhança obtida por (2-exchange)");
		System.out.println("4 - Aplicar melhoramento iterativo (hill climbing)");
		System.out.println("5 - Aplicar simulated annealing");
		//System.out.println("6 - Aplicar metaheurística ACO (ant colony optimization)");
		System.out.println("Escolha o exercicio:");
	}
	
	public static Grafo Ex1(Grafo garf){
		Scanner ler= new Scanner(System.in);
		System.out.println("Ex1:\n");
		System.out.print("Quantidade de pontos no plano: "); int n= ler.nextInt();

		System.out.print("Insira o range desejado: "); int m= ler.nextInt();

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
		clearScreen();
		
		System.out.print("      Array de Original: ");
		garf.printArrayPontos();

		switch(opcao){
			case 1: System.out.print("   Permutação de pontos: ");
					garf.permutation();
					garf.printArrayPontos(); //NOVA ORDEM
				    break;

			case 2: System.out.print("Nearest-neighbour first: ");
					garf.nnf();
					garf.printArrayPontos();
					break;
			default: System.out.println("Opção errada, tente novamente!");

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
		clearScreen();

		switch(opcao){
			case 1: System.out.print("Original: ");
					garf.printArrayPontos();
					garf.hillClimbing(1);
					//garf.printArrayPontos();
				    break;

			case 2: System.out.print("Original:");
					garf.printArrayPontos();
					garf.hillClimbing(2);			
					//garf.printArrayPontos();
					break;

			case 3:	System.out.print("Original:");		
					garf.printArrayPontos();
					garf.hillClimbing(3);
					break;

			case 4: System.out.print("Original:");	
					garf.printArrayPontos();
					garf.hillClimbing(4);
					break;
			default: System.out.println("Opção errada, tente novamente!");
					

		}
		return garf;
	}

	public static Grafo Ex5(Grafo garf){
		clearScreen();
		System.out.println("Ex5:\n");
		System.out.println("Aplicar simulated annealing. Usar como medida de custo o número de cruzamentos de arestas.");
		System.out.println("Original:  ");
		garf.printArrayPontos();
		garf.simA();
		//garf.printArrayPontos();
		return garf;
	}
}