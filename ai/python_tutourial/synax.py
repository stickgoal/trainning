x = 42
print(type(x))
x = "hello"
print(type(x))
x = [1,2,3]
print(type(x))

print(isinstance(x,list))

class Duck:
    def quack(self):
        print("呱！")
class Person:
    def quack(self):
        print("哈哈")

def invoke( d:Duck):
    d.quack()

invoke(Duck())
invoke(Person())