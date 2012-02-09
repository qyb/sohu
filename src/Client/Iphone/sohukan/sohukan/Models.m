//
//  Article.m
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "Models.h"

@implementation Article
    @synthesize key;
    @synthesize title;
    @synthesize url;
    @synthesize download_url;
    @synthesize cover;
    @synthesize category;
    @synthesize is_star;
    @synthesize is_read;
    @synthesize is_download;
    @synthesize is_delete;

@end

@implementation Image
    @synthesize key;
    @synthesize url;
    @synthesize is_download;

@end

@implementation Category
    @synthesize name;

@end