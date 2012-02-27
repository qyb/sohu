//
//  NSURLProtocolCustom.m
//  sohukan
//
//  Created by  on 12-2-21.
//  Copyright (c) 2012年 __MyCompanyName__. All rights reserved.
//

#import "NSURLProtocolCustom.h"

@implementation NSURLProtocolCustom
+ (BOOL)canInitWithRequest:(NSURLRequest*)theRequest
{
    if ([theRequest.URL.scheme caseInsensitiveCompare:@"http"] == NSOrderedSame) {
        return YES;
    }
    return NO;
}

+ (NSURLRequest*)canonicalRequestForRequest:(NSURLRequest*)theRequest
{
    return theRequest;
}

- (void)startLoading
{
    NSURLResponse *response = [[NSURLResponse alloc] initWithURL:self.request.URL 
                                                        MIMEType:@"image/png" 
                                           expectedContentLength:-1 
                                                textEncodingName:nil];
    NSString *key = [[self.request.URL.absoluteString componentsSeparatedByString:@"/"] lastObject];
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentsDirectory = [paths objectAtIndex:0];
    NSString *imagePath = [documentsDirectory stringByAppendingPathComponent:key];
    NSData *data = [NSData dataWithContentsOfFile:imagePath];
    [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
    [[self client] URLProtocol:self didLoadData:data];
    [[self client] URLProtocolDidFinishLoading:self];
    [response release];
}

- (void)stopLoading
{
    NSLog(@"Stop loading");
}

@end
