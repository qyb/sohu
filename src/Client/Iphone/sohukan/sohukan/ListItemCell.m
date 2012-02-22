//
//  ListItemCell.m
//  sohukan
//
//  Created by  on 12-2-14.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "ListItemCell.h"

@implementation ListItemCell
@synthesize article;
@synthesize controller;

-(void)handleSwipe:(UISwipeGestureRecognizer *)recognizer
{
    
    if (recognizer.direction == UISwipeGestureRecognizerDirectionRight){
        
        editViewController = [[EditViewController alloc]
                              initWithNibName:@"EditViewController" bundle:nil];
        editViewController.delegate = self.controller;
        editViewController.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
        editViewController.article = self.article;
        [self.controller presentModalViewController:editViewController animated:YES];
        //NSString *path = [[NSBundle mainBundle] pathForResource:@"edit" ofType:@"png"];
        //UIColor *color = [UIColor colorWithPatternImage:[UIImage imageWithContentsOfFile:path]];
        //_titleLabel.backgroundColor = color;
        //self.textLabel.backgroundColor = color;
    }
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (!self) {
        return nil;
    }
    UISwipeGestureRecognizer* recognizer = [[UISwipeGestureRecognizer alloc] initWithTarget:self action:@selector(handleSwipe:)];
    recognizer.direction = UISwipeGestureRecognizerDirectionRight;
    [self addGestureRecognizer:recognizer];
    [recognizer release];
    

return self;
}

-(void)dealloc
{
    [super dealloc];
}

@end
