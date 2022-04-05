# Calculadora em Python

# Desenvolva uma calculadora em Python com tudo que você aprendeu nos capítulos 2 e 3. 
# A solução será apresentada no próximo capítulo!
# Assista o vídeo com a execução do programa!

print("\n******************* Python Calculator *******************")



print("\n\nSelecione o número da operação desejada:\n\n")

print("1 - Soma\n2 - Subtração\n3 - Multiplicação\n4 - Divisão\n\n\n ")


opcao = input("Digite o número da opção desejada (1, 2, 3 ou 4): ")



def adicao():
    numero_1 = input("Digite o primeiro número: ")
    numero_2 = input("Digite o segundo número: ")
    resultado = int(numero_1) +int(numero_2)
    return print("O resultado da adição é: " + str(resultado))

def subtracao():
    numero_1 = input("Digite o primeiro número: ")
    numero_2 = input("Digite o segundo número: ")
    resultado = int(numero_1) - int(numero_2)
    return print("O resultado da subtração é: " + str(resultado))

def multiplicacao():
    numero_1 = input("Digite o primeiro número: ")
    numero_2 = input("Digite o segundo número: ")
    resultado = int(numero_1) * int(numero_2)
    return print("O resultado da multiplicação é: " + str(resultado))

def divisao():
    numero_1 = input("Digite o primeiro número: ")
    numero_2 = input("Digite o segundo número: ")
    resultado = int(numero_1) / int(numero_2)
    return print("O resultado da divisão é: " + str(resultado))

def erro():
    return print("Opção inválida!")


if opcao == '1':
    adicao()
elif opcao == '2':
    subtracao()
elif opcao == '3':
    multiplicacao()
elif opcao == '4':
    divisao()
else:
    erro()
