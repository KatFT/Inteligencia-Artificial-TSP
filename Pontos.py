import matplotlib.pyplot as plt 



datax = []
datay = []

def myAtoi(string):

	## (x,y)
	initial=0
	for j in range(len(string)):
		if (string[j] == '('):
			initial=j
		
		elif (string[j] == ','):
			datax.append(int(string[initial+1:j]))
			initial=j
		
		elif (string[j] == ')'):
			datay.append(int(string[initial+1:j]))	


m = int (input("Range: "))


myAtoi(input())
datax.append(datax[0])
datay.append(datay[0])
plt.axis([-m,m,-m,m])

plt.plot(datax, datay)

plt.grid(True)
plt.xlabel("eixo x")
plt.ylabel("eixo y")
plt.show()

