% derivative.m
function rtn = showFiguresBedau(data)
[timeLimit,m] = size(data);
head = 0;
usageMin = 1;
usageMax  = 1000;
usages = usageMax-usageMin;

u0 = 20;

X1 = data(:,usageMin+head:usageMax+head);
X2 = zeros(timeLimit,usages);
A = zeros(timeLimit,1);
A2 = zeros(timeLimit,1);
for i = 1:timeLimit
    for j = 1: usages
        X2(i,usages+1-j) = sum(X1(i,usages+1-j:usages));
    end
    A(i) = - ((X2(i,u0+1) - X2(i,u0-1)))/2;
    A2(i) = - ((X2(i,u0+2) - X2(i,u0)))/2;
end

length = 40;
for i = 1:timeLimit
        if (i  & i<timeLimit-length)
             A(i) = sum(A(i:i+length))/(length+1);
             A2(i) = sum(A2(i:i+length))/(length+1);
        end
end

% for i = 1:timeLimit
%     for j = 1: usages
%         if(X1(i,j) > 300 )
%             X1(i,j) = 300;
%         end
%     end
% end

% figure(1);
% mesh(X1(:,1:250)');

% figure(2);
% size(X2)
% mesh(X2(:,10:usages)');
% mesh(X2');
hold on
figure(3);
plot(A,'r');
hold on
plot(A2);
hold off;