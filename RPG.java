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

	//Transforma para um array de pontos
	Point2D[] toarray(){
		return x;
	}
}

class Grafo{
	int tamanho; // Número de nos 
	Point2D[] arrayC; //Array das coordenadas
	Point2D[] bestSoFar; //Usado no hill climbing para determinar o melhor
	LinkedList<Reta> lista;  //Guarda os candidatos
	
	Grafo(int tamanho){
		this.tamanho=0;
		this.arrayC = new Point2D[tamanho];
		this.bestSoFar= new Point2D[tamanho];
		this.lista= new LinkedList<>();
	}

	//Ex1-Funçao geradora de pontos random
	void criacaoPontos(int n,int m){
		long startTime = System.currentTimeMillis();

		double x,y;
		int indice=0;
		Random seed= new Random();
		while(indice<n){
			//int number = random.nextInt(max - min) + min;
			x=(double)seed.nextInt(2*m)-m;
			y=(double)seed.nextInt(2*m)-m;
			if(!verificarPontos(x,y)){ //Vai verificar se existe pontos repetidos
				arrayC[tamanho++]= new Point2D.Double(x,y);
				indice++;
			}
		}
		System.out.println("Novo Array de pontos: ");
		printArrayPontos();
		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
	}

	//Ex1-Verifica se os pontos já existem no array
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
		long startTime = System.currentTimeMillis();
		System.out.println("Array de Original: ");
		printArrayPontos();
		
		System.out.println("\nPermutação de pontos: ");
		Random number= new Random();
		for(int i=1;i<this.tamanho;i++){
			int swap= number.nextInt(this.tamanho);
			Point2D aux= arrayC[swap];
			arrayC[swap]=arrayC[i];
			arrayC[i]=aux;
		}
		printArrayPontos(); //NOVA ORDEM
		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
	}

	//Ex2.2-Nearest-neighbour first
	void nnf(){
		long startTime = System.currentTimeMillis();
		System.out.println("Array de Original: ");
		printArrayPontos();
		System.out.println("\nNearest-neighbour first: ");
					
		int noInicial;
		Random number= new Random(); 
		noInicial=number.nextInt(this.tamanho); //Escolhe o nó inicial
		Point2D aux=new Point2D.Double(); //Para ajudar na troca de posiçoes
		double  minDist; //Guarda a distancia minima
		int indicemin=0,cont;

		//Fazer a troca da posição inicial
		if(noInicial!=0){
			aux=arrayC[0];
			arrayC[0]=arrayC[noInicial];
			arrayC[noInicial]=aux;	
		}	

		for(int j=0; j<this.tamanho-1;j++){
			cont=j+1;
			minDist=arrayC[cont].distance(arrayC[j]); //distancia minima inicial
			indicemin=cont; //guarda o indice do valor minimo
			
			cont++;
			while(cont<this.tamanho){
				if(arrayC[j].distance(arrayC[cont]) < minDist){
					minDist=arrayC[j].distance(arrayC[cont]);
					indicemin=cont;
				}
				cont++;
			}
			//organizar o array, minimo passa para o lado esquerdo (i+1)
			Point2D a = arrayC[indicemin]; 
			arrayC[indicemin]=arrayC[j+1];
			arrayC[j+1]=a;
		}
		printArrayPontos(); //NOVA ORDEM
		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
	}

	//Ex3 e 4-Determinar a vizinhança obtida por (2-exchange)
	void exchange(int op){
		lista.clear();
		Point2D[] nvarry;

		//De acordo com as opçoes copia os valores do array a escolher 
		if(op==1)
			nvarry=arrayC.clone();
		else
			nvarry=bestSoFar.clone();

		//pontos da reta 1: (i-1)<->(i)
		//pontos da reta 1: (b)<->(a)
		Point2D[] novoarray;
		int a=0,b=0;
		for(int i=1;i<this.tamanho-2;i++){
			for(int j=i+1;j<this.tamanho;j++){
				//Caso chegue a ultima reta, ultimo elemento com o primeiro (tamanho-1 -> 0)
				if(j+1==this.tamanho){
					a=0;
					b=this.tamanho-1;
				}
				else{
					a=j+1;
					b=j;
				}

				//pontos da reta1 e da reta2 têm que ser diferentes
				if(b!=(i-1) && b!=i && a!=i && a!=(i-1)){
					if(intersecao(nvarry[i-1],nvarry[i],nvarry[b],nvarry[a])){
						//Imprime as trocas
						//System.out.print("\n \n("+(i-1)+","+i+") -> (" +b+","+ a +")\n");
						//System.out.print((lista.size())+": ");
						//System.out.print("("+(int)nvarry[i].getX()+","+(int)nvarry[i].getY()+")");
						//System.out.println("->("+(int) nvarry[b].getX()+","+(int) nvarry[b].getY()+")\n");

						if(b<i)	
							novoarray=reverse(b,i,nvarry.clone());
						else 
							novoarray=reverse(i,b,nvarry.clone());

						lista.addLast(new Reta(novoarray));
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

		if((o1==0 &&o2==0 && o3==0 &&o4==0) && produtoVet(p1,q1,p2,q2)>0)	return true;

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

	//Ex3 e 4-Reverte o restante array depois do exchange
	Point2D[] reverse(int i,int a, Point2D[] novoarray){
		for(int j=i;j<a;j++){
			Point2D aux= novoarray[j];
			novoarray[j]=novoarray[a];
			novoarray[a]=aux;
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
	    double val = ((q.getY()-p.getY()) * (r.getX()-q.getX()) - (q.getX()-p.getX()) * (r.getY()-q.getY()));
	  
	    if (val == 0) return 0; //p, q e r são colineares
	    return (val > 0)? 1: 2; //1-sentido horário ou 2-sentido anti-horário 
	}

	//Ex3 e 4-Produto Vetorial
	double produtoVet(Point2D p1, Point2D q1, Point2D p2, Point2D q2){
		Point2D x= new Point2D.Double(q1.getX()-p1.getX(),q1.getY()-p1.getY());
		Point2D y= new Point2D.Double(q2.getX()-p2.getX(),q2.getY()-p2.getY());
		return (x.getX()*y.getX()) + (x.getY()*y.getY());
	}
		
	//Ex 4-Algoritmo HiilClimbing com menor perimentro ou menor interseções
	void hillClimbing(int op){
		long startTime = System.currentTimeMillis();
		this.bestSoFar=arrayC; //estado inicial
		double perimetroMin=perimetro(this.bestSoFar);
		double pBest=0.0, pCand=0.0; //Perimentros
		int iBest=0, iCand=0; //Interseções
		int cont=0; //contador de iterações

		exchange(2); //cria a lista de candidatos de acordo com o bestSoFar
		while(!lista.isEmpty()){
			Point2D[] candidate= opcao(op); //melhor candidato de acordo com a opçao escolhida 
			pBest=perimetro(this.bestSoFar);
			pCand=perimetro(candidate);

			boolean op3=false; //validar a opção 3 
			if(op==3){
				iBest=inter(this.bestSoFar);
				iCand=inter(candidate);
				if(iCand<iBest)
					op3=true;
			}
			if(pCand<pBest || op3){					
				bestSoFar=candidate;
				perimetroMin=pCand;
				exchange(2);	
			}
			cont++;
		}
		arrayFinal(bestSoFar,perimetroMin);	

		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
		System.out.println("Iterações do programa: " +cont);
	}
	
	//Ex4 e 5-Opçoes para o hill climbing
	Point2D[] opcao(int op){
		int indiceMin;
		switch(op){
			case 1: //minimo perimetro
					indiceMin=0;
					double pBest=Double.MAX_VALUE; 
					int pos=0; //guarda posiçao

					for(Reta cand : lista){ //para cada posiçao da lista
						double pCand=perimetro(cand.toarray());//calculamos o perimetro
						if(pCand<pBest){//se for menor, troca e muda a posiçao para computar a metrica
							pBest=pCand;
							pos=indiceMin;//computa a metrica
						}
						indiceMin++;//senao avança
					}
					return lista.remove(pos).toarray(); //retiramos da lista para ser avaliado
					
			case 2: return lista.removeFirst().toarray(); //retiramos o primeiro elemento da lista
					
			case 3: //retira o elemento da lista com menos conflitos
					int cont=0 , iBest= Integer.MAX_VALUE, iCand=0;
					indiceMin=0;

					for(Reta cand : lista){
						Point2D[] b= cand.toarray();
						iCand=inter(b);
						if(iCand<iBest){
							iBest=iCand;
							indiceMin=cont;
						}
						cont++;
					}
					return lista.remove(indiceMin).toarray();
					
			case 4: //retiramos um random da lista para ser avaliado
					Random s= new Random();
					return lista.remove(s.nextInt(lista.size())).toarray();
			default: return null;
					
		}

	}

	//Ex4.3-Funçao para saber o número de interseçoes 
	int inter(Point2D[] array){
		int cont=0;
		for(int i=1;i<this.tamanho;i++){
			for(int j=i+1;j<this.tamanho;j++){
				if(j!=(i-1) && j!=i && j-1!=i && j-1!=(i-1)){
					if(intersecao(array[i-1],array[i],array[j-1],array[j]))
					cont++;
				}
			}
		}
		return cont;
	}

	//Ex5-Simulated annealing, medida de custo cruzamentos de arestas
	void simA(){
		long startTime = System.currentTimeMillis();
		System.out.println("Original:  ");
		garf.printArrayPontos();

		this.bestSoFar=arrayC;
		double auxTemp=(double)inter(arrayC); //temperatura
		double perimetro=0.0;
		int cont=1;
		Point2D[] candidate;
		exchange(2);

		while(!lista.isEmpty() && auxTemp>0){
			candidate=opcao(3);
			double pBest=perimetro(this.bestSoFar);
			double pCand=perimetro(candidate);
			
			if(acceptanceProbability(pBest, pCand, auxTemp)==1){
				bestSoFar=candidate;
				perimetro=pCand;
				exchange(2);
			}
			//atualizar a temperatura
			auxTemp=(double) 0.95*auxTemp;
			cont++;
		}
		arrayFinal(bestSoFar,perimetro);
		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
		System.out.println("Iterações do programa: " +cont);
	}

	//Ex5-Função se aceita o candidato
	double acceptanceProbability(double min, double max, double aux) {
        //se for aceite o candidato
		if (max < min) 
            return 1;
        
		//caso nao seja aceite
        return Math.exp((min - max) / aux);
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

	//Ex3 e 4-Imprime a lista de valores
	void printLista(){
		for(int i=0;i<this.lista.size();i++){
			System.out.print(i+": ");
			for(int j=0; j<this.tamanho;j++)
				System.out.print("("+(int)this.lista.get(i).x[j].getX()+","+(int) this.lista.get(i).x[j].getY()+")  ");

			System.out.println();
		}
		System.out.println();
	}

	//Ex 4 e 5-Imprime a solução final
	void arrayFinal(Point2D[] a,double perimetro){
		System.out.println("\nArray Solução: ");
		for(int i=0;i<this.tamanho;i++){
			System.out.print("("+(int)a[i].getX() + ","+(int)a[i].getY()+")");
		}
		System.out.println("\nPerimetro: "+perimetro+"\n");
	}

	//Imprime a lista de valores
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
		boolean op1=false, op2=false;
		do{
			MenuExercicios();
			opcao=ler.nextInt();
			clearScreen();
			//Fazer criação do array, caso nao escolha a opçao 1
			if(opcao>1 && opcao<6){
				if(op1==false){
					op1=true;
					System.out.println("ATENÇÃO: Para fazer o exercicio, temos que criar o array de pontos!\n");
					garf=Ex1(garf);
				}
				if(op2==false && opcao!=2){
					op2=true;
					System.out.println("\n\n\nATENÇÃO:Para fazer o exercicio, tem que escolher um candidato a solução!\n");
					garf=Ex2(garf);
				}
			}
			clearScreen();
			switch(opcao){
				case 1: garf= Ex1(garf);
						op1=true;
						break;
				case 2: garf=Ex2(garf);
						op2=true;
						break;
				case 3: garf=Ex3(garf);
						break;
				case 4: garf=Ex4(garf);
						break;
				case 5: garf=Ex5(garf);
						break;
				default: System.out.println("Opção errada, tente novamente!");
			}
			System.out.print("\n\n0(Sair) / Outro Número (Continuar)   ");
			opcao=ler.nextInt();
		}while(opcao!=0);
	}
	
	//Limpar o ecrã
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
		System.out.print("Quantidade de pontos no plano: "); 
		int n= ler.nextInt();

		System.out.print("Insira o range desejado: "); 
		int m= ler.nextInt();

		garf= new Grafo(n); 
		garf.criacaoPontos(n,m);

		return garf;
	}

	public static Grafo Ex2(Grafo garf){
		Scanner ler= new Scanner(System.in);
		int opcao=0;
		System.out.println("Ex2:\n ");
		System.out.println("Escolha uma das seguintes alternativas para criar ligações:");
		System.out.println("1-Gerar uma permutação qualquer dos pontos.");
		System.out.println("2-Heuristica 'nearest-neighbour first'");
		opcao=ler.nextInt();
		clearScreen();
		
		switch(opcao){
			case 1: garf.permutation();
				    break;

			case 2: garf.nnf();
					break;
			default: System.out.println("Opção errada, tente novamente!");

		}
		return garf;
	}

	public static Grafo Ex3(Grafo garf){
		Scanner ler= new Scanner(System.in);

		//Caso escolha o exercicio 3,  ele mostra a lista
		//Caso a função for chamada no exercicio 4, ele so cria a lista
		System.out.println("\n\nPretende visualizar a lista de candidatos? ");
		System.out.println("0-Sim, Outro-Não");
		int opcao=ler.nextInt();
		clearScreen();

		System.out.println("Ex3:\n");
		System.out.println("Array de Original: ");
		garf.printArrayPontos();
		System.out.println("Vizinhaça 2-exchange:  ");

		long startTime = System.currentTimeMillis();
		garf.exchange(1);
				
		if(opcao==0)
			garf.printLista();
		System.out.println("Números de elementos da lista: "+garf.lista.size());

		long endTime = System.currentTimeMillis();
		System.out.println("\nDemorou " + (endTime - startTime) + " millisegundos");
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

		System.out.println("Original: ");
		garf.printArrayPontos();
		switch(opcao){
			case 1: garf.hillClimbing(1);
				    break;

			case 2: garf.hillClimbing(2);			
					break;

			case 3:	garf.hillClimbing(3);
					break;

			case 4: garf.hillClimbing(4);
					break;
			default: System.out.println("Opção errada, tente novamente!");
		}
		return garf;
	}

	public static Grafo Ex5(Grafo garf){
		clearScreen();
		System.out.println("Ex5:\n");
		System.out.println("Aplicar simulated annealing. Usar como medida de custo o número de cruzamentos de arestas.");
		garf.simA();
		return garf;
	}
}