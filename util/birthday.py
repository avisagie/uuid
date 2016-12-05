
# epoch uuids with 32 bits of timestamp in the msb: who does adding more time
# bits at the cost of more "random" bits affect probability of collisions?

import math
import matplotlib.pylab as P
from decimal import Decimal

def collision(_n, _d):
    n = Decimal(_n)
    d = Decimal(_d)
    x = (-n*(n-1)) / (2*d)
    approx = Decimal(1.0) - x.exp()

    return float(approx.log10())


def verify():
    """
    Recreates parts of the table at https://en.wikipedia.org/wiki/Birthday_problem#Probability_table
    Purpose is to verify the approximation against something.
    """
    import io
    table = """
32 	2 	2 	2 	2.9 	93 	2.9×103 	9.3×103 	5.0×104 	7.7×104 	1.1×105
40 	2 	2 	2 	47 	1.5×103 	4.7×104 	1.5×105 	8.0×105 	1.2×106 	1.7×106
48 	2 	2 	24 	7.5×102 	2.4×104 	7.5×105 	2.4×106 	1.3×107 	2.0×107 	2.8×107
64 	6.1 	1.9×102 	6.1×103 	1.9×105 	6.1×106 	1.9×108 	6.1×108 	3.3×109 	5.1×109 	7.2×109
96 	4.0×105 	1.3×107 	4.0×108 	1.3×1010 	4.0×1011 	1.3×1013 	4.0×1013 	2.1×1014 	3.3×1014 	4.7×1014
128 	2.6×1010 	8.2×1011 	2.6×1013 	8.2×1014 	2.6×1016 	8.3×1017 	2.6×1018 	1.4×1019 	2.2×1019 	3.1×1019
192 	1.1×1020 	3.5×1021 	1.1×1023 	3.5×1024 	1.1×1026 	3.5×1027 	1.1×1028 	6.0×1028 	9.3×1028 	1.3×1029
256 	4.8×1029 	1.5×1031 	4.8×1032 	1.5×1034 	4.8×1035 	1.5×1037 	4.8×1037 	2.6×1038 	4.0×1038 	5.7×1038
384 	8.9×1048 	2.8×1050 	8.9×1051 	2.8×1053 	8.9×1054 	2.8×1056 	8.9×1056 	4.8×1057 	7.4×1057 	1.0×1058
512 	1.6×1068 	5.2×1069 	1.6×1071 	5.2×1072 	1.6×1074 	5.2×1075 	1.6×1076 	8.8×1076 	1.4×1077 	1.9×1077
    """
    inp = io.StringIO(table)
    probs = [1e-18, 1e-15, 1e-12, 1e-9, 1e-6, 0.001, 0.01, 0.25, 0.50, 0.75]
    for ii, line in enumerate(inp):
        if not line.strip(): continue
        tokens = line.strip().split()
        bits = int(tokens[0])
        counts = [float(s.replace('×10', 'e0')) for s in tokens[1:]]

        print()
        print(bits)
        for expected, count in zip(probs, counts):
            approx = collision(count, 1<<bits)
            print("expected %01.01f, got %01.01f" % (math.log10(expected), approx))
        

verify()

years = 10

# events per second
rate = 400000.0

# over and above the 32 bits already used for the timestamp up to seconds resolution
n = range(16)

# some many random bits are left in the uuid
random_bits = 96

# rates per time window with so many bits used for the timestamp
x = [rate/(1<<i) for i in n]
p = [collision(rate/(1<<i), math.pow(2.0, random_bits-i)) for i in n]
y = [(Decimal(1) - ((Decimal(1) - Decimal(math.pow(10,p[i]))) ** ((1<<i) * 24 * 3600 * 365 * years))).log10() for i in n]

print("bits: events/s\n", "\n".join(["  %02d: %01.01f" % (b,x) for (b,x) in zip(n,x)]))

P.subplot(3,1,1)
P.plot(n, x, '-+')
P.title('events/s for so many bits extra in timestamp')
P.grid()

P.subplot(3,1,2)
P.plot(n, p, '-+')
P.title('$log_{10}$(probability of collision per timerange)')
P.grid()

P.subplot(3,1,3)
P.plot(n, y, '-+')
P.title('$log_{10}$(Probability of one or more timeranges with collisions in ' + str(years) + ' years)')
P.xlabel('bits added to timestamp')
P.grid()

P.show()


