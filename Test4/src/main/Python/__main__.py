import numpy as np  # Python本身没有处理矩阵的数据类型，因此需要使用附加的函数库。

"""
1. 读取csv文件
"""


def prin(path):
    tmp = np.loadtxt(path, dtype=np.str, delimiter=",")
    tmp = tmp.astype(float)  # 类型转换
    return tmp


"""
2. 方根法求单层元素相对重要性
"""


def ReImpo(F):
    n = np.shape(F)[0]  # F矩阵的行数，np.shape(F)[1]为列数
    W = np.zeros([1, n])  # 生成1行n列的0矩阵
    for i in range(n):
        t = 1
        for j in range(n):
            t = F[i, j] * t  # 累乘
        W[0, i] = t ** (1 / n)  # 开n次方根求Wi
    W = W / sum(W[0, :])  # 归一化
    return W.T


"""
3. 一致性检验
"""


def isConsist(F, RI):
    n = np.shape(F)[0]  # F矩阵的行数，np.shape(F)[1]为列数
    W = np.zeros([1, n])
    λmi = np.zeros([1, n])
    λmax = 0
    for i in range(n):
        t = 1
        for j in range(n):
            t = F[i, j] * t  # 累乘
        W[0, i] = t ** (1 / n)  # 开n次方根求Wi
    for i in range(n):
        temp = 0
        for j in range(n):
            temp = F[i, j] * W[0, j] + temp
        λmi[0, i] = temp / W[0, i]
        λmax = λmax + λmi[0, i] / n
    CI = (λmax - n) / (n - 1)
    CR = CI / RI
    if CR <= 0.1:
        return bool(1)
    else:
        return bool(0)


"""
4. 计算综合重要性
"""


def ComImpo(F12, F231, F232, F233):  # 综合重要性
    if isConsist(F12, 0.58) and isConsist(F231, 1.12) and isConsist(F232, 0.90) and isConsist(F233, 0.90):
        W12 = ReImpo(F12)
        W231 = ReImpo(F231)
        b = np.array([[0]])
        W232 = np.r_[b, ReImpo(F232)]  # np.r_是按列连接两个矩阵
        W233 = np.r_[ReImpo(F233), b]
        W23 = np.hstack([W231, W232, W233])  # np.hstack将参数元组的元素数组按水平方向进行叠加
    else:
        print("判断矩阵不通过一致性检验！")
        return 0
    n = len(W23)
    C = np.zeros([1, n])
    for i in range(n):
        t = W23[i, :]
        C[0, i] = sum((W12.T * t)[0])
    return C


def main():
    F1 = prin("../resources/data1.csv")
    F2 = prin("../resources/data2.csv")
    F3 = prin("../resources/data3.csv")
    F4 = prin("../resources/data4.csv")
    C = ComImpo(F1, F2, F3, F4)
    with open("../resources/out.csv","w")as f:
        f.write("P1-P5五位教师综合推荐指数分别为：\n")
        for i in range(5):
            f.write(str(C[0, i])+"\n")
        f.write("因此评价排序为：\n")
        f.write(str(np.argsort(-C) + 1))  # 将C中的元素从大到小排列，提取其对应的index(索引)
    f.close()

if __name__ == '__main__':
    main()

