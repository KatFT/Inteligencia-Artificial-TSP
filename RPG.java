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

	//Transforma a classe reta para um array de pontos
	Point2D[] toarray(){ 
		return x;
	}
}

class Grafo{
	int tamanho; // nº de nos 
	Point2D[] arrayC; //array das coordenadas
	Point2D[] bestSoFar; //usado no hill climbing para determinar o melhor candidato
	LinkedList<Reta> lista;   //guardar os candidatos
	
	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
		this.bestSoFar= new Point2D[tamanho];
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

	//Ex2.1-Permutação de pontos
	void permutation(){
		Random number= new Random();
		for(int i=1;i<this.tamanho;i++){
			int swap= number.nextInt(this.tamanho);
			Point2D tmp= arrayC[swap];
			arrayC[swap]=arrayC[i];
			arrayC[i]=tmp;
		}
	}

	//Ex2.2-Nearest-neighbour first
	void nnf(){
		int noInicial;
		Random number= new Random();
		noInicial=number.nextInt(this.tamanho);  //escolher o nó inicial
		Point2D aux=new Point2D.Double(); //Para ajudar na troca de posiçoes
		double minDist; //guarda a distancia minima
		int indicemin=0, indComp=0; //guarda o indice minimo encontrado, o indice a comparar aseguir

		//Fazer a troca da posição inicial
		if(noInicial!=0){
			aux=arrayC[0];
			arrayC[0]=arrayC[noInicial];
			arrayC[noInicial]=aux;	
		}	
		
		for(int i=0; i<this.tamanho-1;i++){
			indComp=i+1;
			minDist=arrayC[indComp].distance(arrayC[i]); //distancia minima inicial
			indicemin=indComp; 
			indComp++;

			while(indComp<this.tamanho){
				if(arrayC[i].distance(arrayC[indComp]) < minDist && i!=indComp){
					minDist=arrayC[i].distance(arrayC[indComp]);
					indicemin=indComp;
				}
				indComp++;
			}
			//organizar o array, minimo passa para o lado esquerdo (i+1)
			aux = arrayC[indicemin]; 
			arrayC[indicemin]=arrayC[i+1];
			arrayC[i+1]=aux;
		}
	}

	//Ex3 e 4-Determinar a vizinhança obtida por (2-exchange)
	void exchange(int op){
		lista.clear();
		Point2D[] nvArray;  
		//de acordo com as opçoes copia os valores do array a escolher 
		if(op==1) 
			nvArray=arrayC.clone();
		else
			nvArray=bestSoFar.clone();

		Point2D[] auxArrayCand;

		//r1p1 e r1p2 - pontos da reta1
		//r2p1 e r2p2 - pontos da reta2
		int p1,p2,r1p1; 

		for(int r1p2=1; r1p2<this.tamanho; r1p2++){
			r1p1=r1p2-1;
			for(int r2=r1p2; r2<=this.tamanho; r2++){
				//Caso chegue a ultima reta, ultimo elemento com o primeiro (tamanho-1 -> 0)
				if(r2==this.tamanho){
					p1=0;
					p2=this.tamanho-1;
				}else{
					p1=r2-1;
					p2=r2;
				}

				//pontos da reta1 e da reta2 têm que ser diferentes
				if(p2!=(r1p1) && p2!=r1p2 && p1!=r1p2 && p1!=(r1p1)){
					if(intersecao(nvArray[r1p2], nvArray[r1p1], nvArray[p2], nvArray[p1])){
						if(p1<r1p2)	
							auxArrayCand=reverse(p1,r1p2,nvArray.clone());
						else 
							auxArrayCand=reverse(r1p2,p1,nvArray.clone());

						lista.addLast(new Reta(auxArrayCand));
						if(op==1){
							System.out.print((lista.size())+": ");
							System.out.print("("+(int)arrayC[r1p2].getX()+","+(int)arrayC[r1p2].getY()+")");
							System.out.println("->("+(int) arrayC[p1].getX()+","+(int) arrayC[p1].getY()+")");
						}
					}
				}
			}
		}
	}

	//Ex3 e 4-Verifica se os segmentos se intersetam
	boolean intersecao(Point2D p1, Point2D q1, Point2D p2, Point2D q2) {
		//indica as orientações dos segmentos
	    double o1 = orientation(p1, q1, p2);
	    double o2 = orientation(p1, q1, q2);
	    double o3 = orientation(p2, q2, p1);
	    double o4 = orientation(p2, q2, q1);
	  
		if(o1!=o2 && o3!=o4) return true;
		if((o1==0 &&o2==0 && o3==0 &&o4==0)  && produtoVet(p1,q1,p2,q2)>0)	return true;

	    // p1, q1 e p2 sao colineares e p2 é colinear com p1q1
	    if (o1 == 0 && onSegment(p1, p2, q1)) return true;
	  
	    // p1, q1 e q2 sao colineares e q2 é colinear com p1q1
	    if (o2 == 0 && onSegment(p1, q2, q1)) return true;
	  
	    // p2, q2 e p1 sao colineares e p1 é colinear com p2q2
	    if (o3 == 0 && onSegment(p2, p1, q2)) return true;
	  
	    // p2, q2 e q1 sao colineares e q1 é colinear com p2q2
	    if (o4 == 0 && onSegment(p2, q1, q2)) return true;
	  
	    return false;
	}

	//Ex3 e Ex4-Reverte o restante array depois do exchange
	Point2D[] reverse(int i,int a, Point2D[] novoarray){
		for(int j=i;j<a;j++){
			Point2D temp= novoarray[j];
			novoarray[j]=novoarray[a];
			novoarray[a]=temp;
			a--;
		}
		return novoarray;
	}

	//Ex3 e 4-Verifica se um ponto esta contido numa segmentos
	static boolean onSegment(Point2D p, Point2D q, Point2D r){
	    if (q.getX() <= Math.max(p.getX(), r.getX()) && 
			q.getX() >= Math.min(p.getX(), r.getX()) && 
			q.getY() <= Math.max(p.getY(), r.getY()) && 
			q.getY() >= Math.min(p.getY(), r.getY()))
	    	return true;
	    return false;
	}
	  
	//Ex3 e 4-Encontrar a orientação do trio ordenado (p, q, r).
	static double orientation(Point2D p, Point2D q, Point2D r){
	    double val = ((q.getY() - p.getY()) * (r.getX() - q.getX())-(q.getX() - p.getX()) * (r.getY() - q.getY()));
	    if (val == 0) return 0; //p, q e r são colineares
	    return (val > 0)? 1: 2; //sentido horário ou sentido anti-horário 
	}

	//Ex3 e 4-Produto Vetorial
	double produtoVet(Point2D p1, Point2D q1, Point2D p2, Point2D q2){
		Point2D x= new Point2D.Double(q1.getX()-p1.getX(),q1.getY()-p1.getY());
		Point2D y= new Point2D.Double(q2.getX()-p2.getX(),q2.getY()-p2.getY());
		return (x.getX()*y.getX()) + (x.getY()*y.getY());
	}
	
	//Ex4 e 5-Calcula o perimetro do poligono
	double perimetro(Point2D[] array){
		double soma=0;
		for(int i=1;i<this.tamanho;i++){
			soma+=array[i-1].distance(array[i]);
		}
		soma+=array[0].distance(array[this.tamanho-1]);
		return soma;
	}

	//Ex 4-Algoritmo para calcular o array aonde o perimetro é minimo
	void hillClimbing(int op){
		double result=0.0, perimBest=0.0, perimCand=0.0;
		Point2D[] candidate,aux;
		this.bestSoFar=arrayC; //candidato (pai)
		exchange(2); //cria a lista de candidatos (filhos)
		

		while(!lista.isEmpty()){
			candidate= opcao(op); //melhor candidato de acordo com a opçao escolhida 
			aux=candidate; //auxiliar para troca de pontos  

			perimBest=perimetro(this.bestSoFar);
			perimCand=perimetro(candidate);

			if(perimCand<perimBest){
				result=perimCand;
				bestSoFar=candidate;		
				exchange(2);					
			}else{
				//troca do candidato com o best 
				aux=candidate;
				candidate=bestSoFar;	
				bestSoFar=aux;
				System.out.println("Passou a frente!");
				exchange(2);
			}
		}
		arraySolucao(bestSoFar,result);	
	}

	//Ex4 e 5-Opçoes para o hill climbing
	Point2D[] opcao(int op){
		switch(op){
			case 1: //minimo perimetro
					int i=0, pos=0;
					double j=Double.MAX_VALUE, calc;
					
					for(Reta res : lista){
						calc=perimetro(res.toarray());
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

	//Ex 4 Imprime a solução final
	void arraySolucao(Point2D[] a,double res){
		System.out.print("\nArray Solução: ");
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)a[i].getX() + ","+(int)a[i].getY()+")");
		}
		System.out.println();
		System.out.println("Perimetro: "+res);
		System.out.println();
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


	double acceptanceProbability(double min, double max, double temp) {
        // If the new solution is better, accept it
        if (max < min) {
            return 1;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((min - max) / temp);
    }

	void simA(){
		this.bestSoFar= arrayC;
		double temp= (double)inter(arrayC); //temperatura
		exchange(2);
		double res=0.0;
		while(!lista.isEmpty() && temp>0){

			Point2D[] candidate= opcao(3);
			Point2D[] aux=candidate; //candidato 
			double min=perimetro(this.bestSoFar);
			double max=perimetro(candidate);
			//aceita
			if(acceptanceProbability(min, max, temp)==1){
				bestSoFar= candidate;
				res=max;
				exchange(2);
			}
			else{
				aux=candidate;
				candidate=bestSoFar;	
				bestSoFar=aux;
				exchange(2);
			}
			//atualizar a temperatura
			temp=(double) 0.95*temp;

		}
		arraySolucao(bestSoFar,res);

	}

	//Ex4 Imprime a lista de valores
	void printLista(){
		for(int i=0;i<this.lista.size();i++){
			System.out.print(i+": ");
			for(int j=0; j<this.tamanho;j++)
				System.out.print("("+(int)this.lista.get(i).x[j].getX()+","+(int) this.lista.get(i).x[j].getY()+")");

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
		garf.exchange(1);
		System.out.println();
		garf.printLista();
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
				    break;

			case 2: System.out.print("Original:");
					garf.printArrayPontos();
					garf.hillClimbing(2);			
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
		return garf;
	}
}