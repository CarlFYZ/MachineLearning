% derivative.m
function rtn = showFiguresNet(data)
[m,n] = size(data);
avg = 50;

X1 = data(:,1);
X2 = data(:,2);

for i = 1:m-100
        XX1(i) = sum(X1(i:i+avg))/(avg+1);
        XX2(i) = sum(X2(i:i+avg))/(avg+1);
end
figure(1);
plot(XX1);
grid on;
hold on;
% figure(2);
plot(XX2,'r');
