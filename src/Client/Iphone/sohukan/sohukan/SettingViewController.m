//
//  SettingViewController.m
//  sohukan
//
//  Created by  on 12-2-22.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "SettingViewController.h"


@implementation SettingViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [self.navigationController setToolbarHidden:YES];
    [self.navigationController setNavigationBarHidden:NO];
    [super viewDidLoad];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 3;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return @"账号设置";
            break;
        case 1:
            return @"数据设置";
        default:
            return @"关于";
            break;
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section
{
    return 20.0;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return 2;
            break;
        case 1:
            return 3;
            break;
        default:
            return 3;
            break;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    NSInteger row = [indexPath row];
    UITableViewCell *cell;
    switch ([indexPath section]) {
        case 0:
            if (row == 0) {
                cell = [tableView dequeueReusableCellWithIdentifier:@"NameCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"NameCell"]   autorelease];
                    cell.textLabel.text = @"账号";
                }
                return cell;
            }else{
                cell = [tableView dequeueReusableCellWithIdentifier:@"PasswordCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"PasswordCell"]   autorelease];
                    cell.textLabel.text = @"密码";
                }
                
                }
            break;
        case 1:
            if (row == 0){
                cell = [tableView dequeueReusableCellWithIdentifier:@"SyncCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"SyncCell"] autorelease];
                    cell.textLabel.text = @"仅在WIFI下同步";
                }
                return cell;
            }else if (row == 1){
                cell = [tableView dequeueReusableCellWithIdentifier:@"ImageCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"ImageCell"] autorelease];
                    cell.textLabel.text = @"下载图片";
                }
            
            }else{
                cell = [tableView dequeueReusableCellWithIdentifier:@"CaheCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"CacheCell"] autorelease];
                    cell.textLabel.text = @"清除缓存";
                }
            
            
            }
            break;
        default:
            if (row == 0){
                cell = [tableView dequeueReusableCellWithIdentifier:@"VersioinCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"VersionCell"] autorelease];
                    cell.textLabel.text = @"检查版本";
                    UIImage *image = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"check_version" ofType:@"png"]];
                    cell.imageView.image = image;
                }
                return cell;
            }else if(row == 1){
                cell = [tableView dequeueReusableCellWithIdentifier:@"AboutCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"AboutCell"] autorelease];
                    cell.textLabel.text = @"使用引导";
                    UIImage *image = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"introduce" ofType:@"png"]];
                    cell.imageView.image = image;
                    cell.imageView.contentMode =UIViewContentModeScaleToFill;
                }
                return cell;
            }else{
                cell = [tableView dequeueReusableCellWithIdentifier:@"FeedBackCell"];
                if (cell == nil){
                    cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"FeedBackCell"] autorelease];
                    cell.textLabel.text = @"意见反馈";
                    UIImage *image = [UIImage imageWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"feedback" ofType:@"png"]];
                    cell.imageView.image = image;
                }
                return cell;
            }
            break;
    }
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier] autorelease];
    }
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    
}

@end
