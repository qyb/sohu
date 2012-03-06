//
//  GuideViewController.m
//  sohukan
//
//  Created by  on 12-2-29.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "GuideViewController.h"

@implementation GuideViewController
@synthesize images;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)showAnimation
{
    [UIView beginAnimations:nil context:nil];
    [UIView setAnimationTransition:UIViewAnimationOptionTransitionFlipFromRight forView:self.view cache:YES];
    [UIView setAnimationDuration:10.3];
    [UIView commitAnimations];
}

-(IBAction)handleRightSwipe:(UISwipeGestureRecognizer *)recognizer
{
    switch (imageCount) {
        case 0:
            break;
        case 1:
            imageCount -= 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
        case 2:
            imageCount -= 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
        case 3:
            imageCount -= 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
        default:
            break;
    }

}

-(IBAction)handleLeftSwipe:(UISwipeGestureRecognizer *)recognizer
{
    switch (imageCount) {
        case 0:
            imageCount += 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
            break;
        case 1:
            imageCount += 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
            break;
        case 2:
            imageCount += 1;
            [self.view addSubview:[self.images objectAtIndex:imageCount]];
            break;
        default:
            break;
    }
    
}


- (void)viewDidLoad
{   
    imageCount = 0;
    [[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleBlackOpaque animated:YES];
    UIImageView *guide1 = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"guide_1" ofType:@"png"]]];
    UIImageView *guide2 = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"guide_2" ofType:@"png"]]];
     UIImageView *guide3 = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"guide_3" ofType:@"png"]]];
     UIImageView *guide4 = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"guide_4" ofType:@"png"]]];
    [self.view addSubview:guide1];
    self.images = [NSArray arrayWithObjects:guide1, guide2, guide3, guide4, nil];
    [guide1 release];
    [guide2 release];
    [guide3 release];
    [guide4 release];
    UISwipeGestureRecognizer* leftRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleLeftSwipe:)];
    leftRecognizer.direction = UISwipeGestureRecognizerDirectionLeft;
    [self.view addGestureRecognizer:leftRecognizer];
    [leftRecognizer release];
    UISwipeGestureRecognizer* rightRecognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleRightSwipe:)];
    rightRecognizer.direction = UISwipeGestureRecognizerDirectionRight;
    [self.view addGestureRecognizer:rightRecognizer];
    [rightRecognizer release];
    [super viewDidLoad];
}


- (void)viewDidUnload
{
    [_mainView release];
    self.images = nil;
    [super viewDidUnload];
    
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

@end
