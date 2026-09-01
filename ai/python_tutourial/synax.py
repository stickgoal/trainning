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

print(test(10,15))

n = 7
if n > 10 :
    print("大于10")
elif n >8 :
    print("大于8")
else :
    print("小于8")
print('='*50)
students = ['zhangsan','lisi','王五']
for s in students:
    print(s)

print('='*50)
for idx,e in enumerate(['张三','李四','王五']):
    print(f'第{idx+1}同学：{e}')
print('='*50)
for stu,proj in zip(['张三','李四','王五'],['换电项目','共享车位项目','AI问诊']):
    print(f'{stu}做的是{proj}项目')

print('='*50)

nums=range(1,6)
print(nums)

squares = [x*x for x in nums]
print(squares)
#        变量        集合    筛选条件
#         ↓           ↓    [    ↓   ]
evens = [ x for x in nums if x% 2==0]
print(evens)

print('+'*30)

words = ['hello','world']
# 将集合按照某个规则转换为字典
upper_case_words ={ w : w.upper() for w in words}
print(upper_case_words)


# 将集合按照某个规则转换为一个集合

def map2Chinese(raw: str) -> str:
    match raw:
        case 'a': return '甲'
        case 'b': return '乙'
        case 'c': return '丙'
        case 'e': return '丁'
        case 'd': return '戊'
        case 'f': return '己'
        case 'g': return '庚'
        case 'h': return '辛'
        case 'i': return '壬'
        case 'j': return '癸'
        case _: return '某'
        

alphabets = list('helloword')
chinese = [map2Chinese(a) for a in alphabets]
print(chinese)


celsius = [18, 25, 30, 22, 35]

fahrenheit = [c*9/5+32 for c in celsius if c>=25]
print(fahrenheit)

words = ["ai", "agent", "mcp"]
lens = [len(w) for w in words]
print(lens)

def raiseSomeError(e):
    if(e==0):
        raise ValueError("除数不能为0")

try:
    raiseSomeError(0)
except ValueError as e:
    print(f'出错了：{e}')
finally:
    print("最终执行...")