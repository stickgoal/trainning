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
# :str 不是声明，而是注释。 类型依然可以改。
s:str = "hello"

s = 13
print(f"变量s的值是{s},类型是：{type(s)}")

def test(a:int,b:int) -> int:
    return a + b

if __name__ == "__main__":
    print(test(10,15))
