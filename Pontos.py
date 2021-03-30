import matplotlib.pyplot as plt 

# Python program for
# implementation of atoi

# A utility function to check
# whether x is numeric


def isNumericChar(x):
	if (x >= '0' and x <= '9'):
		return True
	return False

# A simple atoi() function.
# If the given string contains
# any invalid character,
# then this function returns 0

datax =[]
datay =[]

def myAtoi(string):
	if len(string) == 0:
		return 0

	res = 0
	# initialize sign as positive
	sign = 1

	# Iterate through all characters
	# of input string and update result

	x=1

	for j in range(len(string)):
		if (string[j] == '('):
			continue

		elif (string[j] == ')' or string[j] == ',' ):
			#guardar res em lista
			if x==1:
				datax.append(sign*res)
			else:
				datay.append(sign*res)
			#sign*res
			sign=1
			res=0
			#inverter de guardar na lista x-> lista->y	
			if x==1:
				x=0
			else:
				x=1

		elif (string[j]=='-'):
			sign =-1

		else:
			res = res * 10 + (ord(string[j]) - ord('0'))

# Driver code
# This code is contributed by BHAVYA JAIN

m = int (input("Range: "))

"""
for item in range(n):
	x = int (input())
	y = int (input())
	
	datax.append(x)
	datay.append(y)
"""

s = input()
myAtoi(s)

plt.axis([-m,m,-m,m])
datax.append(datax[0])
datay.append(datay[0])



plt.plot(datax, datay)

plt.grid(True)
plt.xlabel("eixo x")
plt.ylabel("eixo y")
plt.show()