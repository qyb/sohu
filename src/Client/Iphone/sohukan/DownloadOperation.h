/*
DownloadOperation.h

Author: Makoto Kinoshita

Copyright 2009 HMDT. All rights reserved.
*/

#import <Foundation/Foundation.h>

@interface DownloadOperation : NSOperation
{
    NSURLRequest*       _request;
	NSURLConnection*    _connection;
	NSMutableData*      _data;
    NSString*           _key;
    NSString*           _type;
    BOOL                _isExecuting, _isFinished;
}

@property (readonly) NSData* data;
@property (retain, nonatomic) NSString* key;
@property (retain, nonatomic) NSString* type;
- (id)initWithRequest:(NSURLRequest*)request;
@end
