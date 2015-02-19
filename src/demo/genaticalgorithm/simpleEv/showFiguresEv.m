% derivative.m
function rtn = showFiguresEv(data)
[timeLimit,m] = size(data);
head = 2;
usageMin = 0;
usageMax  = timeLimit;
usages = usageMax-usageMin;
extraGeneration = 100;
showLength = 800;
u0 = 40;

rate = data(extraGeneration+1:timeLimit,2);
X1 = data(extraGeneration:timeLimit,usageMin+head:usageMax+head);
X2 = zeros(timeLimit-extraGeneration,usages);
A = zeros(timeLimit-extraGeneration,1);

for i = 1:timeLimit - extraGeneration
    for j = 1: usages
        X2(i,usages+1-j) = sum(X1(i,usages+1-j:usages));
    end
    A(i) = - ((X2(i,u0+1) - X2(i,u0)));
end

% for i = 1:50
%     data(i,1) = 0;
%     data(i,2) = 0;
% end

for i = 1:timeLimit
    for j = 1: usages
        if (j > 1 & j<usages)
%             X1(i,j) = (X1(i,j-1) + X1(i,j) + X1(i,j+1))/3;
        end
%         if (X1(i,j)>300)
%             X1(i,j) = 300;
%         end
    end
end

% showRate = rate*sum(A)/sum(rate);
A = A*sum(rate)/sum(A);

figure(1);
mesh(X1');
% figure(2);
% size(X2)
% mesh(X2(:,10:usages)');

figure(3);
length = 20;
for i = 1:timeLimit
        if (i  & i<timeLimit-extraGeneration-length)
             A(i) = sum(A(i:i+length))/(length+1);
%              showRate(i) = sum(showRate(i:i+length))/(length+1);
             rate(i) = sum(rate(i:i+length))/(length+1);
%              data(i,1) = sum(data(i:i+length,1))/(length+1);
%              data(i,2) = sum(data(i:i+length,2))/(length+1);
        end
end
grid on;
hold on;
% plot(A(u0:timeLimit));
plot(A(1:showLength));
plot(rate(1:showLength),'r');
% plot(data(:,1)*sum(A)/sum(data(:,1)),'r');
% plot(data(:,2)*sum(A)/sum(data(:,2)),'r');
% plot(data(:,1)*sum(A),'r');
% plot(data(:,2)*sum(A),'g');
hold off;