// ConsoleApplication1.cpp : 此文件包含 "main" 函数。程序执行将在此处开始并结束。
//
#include <opencv2/core.hpp>
#include <opencv2/imgcodecs.hpp>
#include <opencv2/highgui.hpp>
#include <iostream>
#include <Windows.h>
#include <time.h>
using namespace cv;
using namespace std;
int vedio();
int pic();

int main(int argc, char** argv)
{
	char key;
	while (cin >> key)
	{
		switch (key)
		{
		case 'p':
			pic();
			break;
		case 'v':
			vedio();
			break;
		case 'q':
			exit(0);
			break;
		}
	}
}

int pic()
{
	VideoCapture cap(0);    //打开摄像头
	if (!cap.isOpened())
	{
		return -1;
	}
	Mat frame;

	int i = 1;
	while (1)
	{
		String filename = format("photo%d.jpg", i);
		char key = waitKey(100);
		cap >> frame;
		imshow("frame", frame);
		switch (key)
		{
		case'p':
			i++;
			imwrite(filename, frame);
			imshow("photo", frame);
			waitKey(0);
			destroyWindow("photo");
			break;
		case 'q':
			exit(0);
			break;
		}
		i++;
	}
}

int vedio()
{
	VideoCapture cap;
	VideoWriter outputVideo;
	Mat frame;
	cap.open(0);// 打开1号摄像头
	if (!cap.isOpened())
	{
		return -1;
	}
	outputVideo.open(String("temp.avi"),
		CV_FOURCC('D','I','V','X'),
		25.0,
		Size(640, 480));// 配置输出视频文件
	while (1) {
		cap >> frame;
		outputVideo.write(frame);// 将该帧写入视频文件
		imshow("Recording...", frame);// 展示图片
		if (waitKey(250) == 'q')  break; // 等待250ms，期间如果有按下空格，则执行break
	}

	destroyWindow("Recording...");// 在释放cap之前，要销毁所有的显示图像窗口
	cap.release();
	outputVideo.release();

	return 0;
}
// 运行程序: Ctrl + F5 或调试 >“开始执行(不调试)”菜单
// 调试程序: F5 或调试 >“开始调试”菜单

// 入门使用技巧: 
//   1. 使用解决方案资源管理器窗口添加/管理文件
//   2. 使用团队资源管理器窗口连接到源代码管理
//   3. 使用输出窗口查看生成输出和其他消息
//   4. 使用错误列表窗口查看错误
//   5. 转到“项目”>“添加新项”以创建新的代码文件，或转到“项目”>“添加现有项”以将现有代码文件添加到项目
//   6. 将来，若要再次打开此项目，请转到“文件”>“打开”>“项目”并选择 .sln 文件
