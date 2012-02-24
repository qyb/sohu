//
//  DatabaseProcess.m
//  sohukan
//
//  Created by riven on 12-2-2.
//  Copyright 2012年 __MyCompanyName__. All rights reserved.
//

#import "DatabaseProcess.h"
#import "Models.h"

@implementation DatabaseProcess

-(id) init
{
    if ((self = [super init]))
    {   
        _db = [self connectionDB];
    }   
    return self;
}


-(FMDatabase *)connectionDB
{    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);  
    NSString *documentDirectory = [paths objectAtIndex:0];  
    NSString *dbPath = [documentDirectory stringByAppendingPathComponent:@"sohukan.db"];  
    FMDatabase *db= [FMDatabase databaseWithPath:dbPath] ;  
    if (![db open]) {  
        NSLog(@"Could not open db.");
        return nil;
    }else{
        return db;
    } 
}

-(void)createArticleTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Article (key TEXT PRIMARY KEY, title TEXT, url TEXT, download_url TEXT, cover TEXT, category TEXT, create_time TEXT, is_star INTEGER, is_read INTEGER, is_download INTEGER)"];

}

-(void)createImageTable
{   
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Image (key TEXT, url TEXT NOT NULL, is_download INTEGER)"];

}

-(void)createCategoryTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Category (name TEXT PRIMARY KEY)"];
}

-(void)createVersionTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Version (version INTEGER PRIMARY KEY)"];
}

-(void)createOperationTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS Operation (type TEXT PRIMARY KEY, method TEXT, content TEXT)"];
}

-(void)createHistoryTable
{
    [_db executeUpdate:@"CREATE TABLE IF NOT EXISTS History (key TEXT, read_time TEXT)"];
}

-(void)insertArticleData:(NSMutableDictionary *)data
{
    NSString *category;
    if ([[[data objectForKey:@"category"] stringValue] length] == 0) {
        category = @"";
    }else{
        category = [[data objectForKey:@"category"] stringValue];
    }
    NSString *sql = [NSString stringWithFormat:@"INSERT INTO Article (key, title, url, download_url, cover, category, create_time, is_star, is_read, is_download) VALUES ('%@', '%@', '%@', '%@', '%@', '%@', '%@', %@, %@, %d)", [data objectForKey:@"key"], [data objectForKey:@"title"], [data objectForKey:@"url"], [data objectForKey:@"download_url"], [data objectForKey:@"cover"], category, [data objectForKey:@"create_time"], [data objectForKey:@"is_star"], [data objectForKey:@"is_read"], 0];
    [_db executeUpdate:sql];
}

-(void)executeUpdate:(NSString *)sql
{
    [_db executeUpdate:sql];
}

-(FMResultSet *)getRowEntity:(NSString *)tableName primaryKey:(NSString *)key
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM %@ WHERE key='%@'", tableName, key];
    FMResultSet *rs = [_db executeQuery:sql];
    return rs;
}

-(int)getVersionId
{
    int res = [_db intForQuery:@"SELECT version FROM Version"];
    if (res){
        return res;
    }else{
        return 0;
    }
}

-(Article *) getArticleInstance:(FMResultSet *)rs dateFormatter:(NSDateFormatter *)dateFormatter
{
    Article *article = [[[Article alloc] init] autorelease];
    article.key = [rs stringForColumn:@"key"];
    article.title = [rs stringForColumn:@"title"];
    article.url = [rs stringForColumn:@"url"];
    article.download_url = [rs stringForColumn:@"download_url"];
    article.cover = [rs stringForColumn:@"cover"];
    article.category = [rs stringForColumn:@"category"];
    article.create_time = [dateFormatter dateFromString:[rs stringForColumn:@"create_time"]];
    article.is_star = [rs boolForColumn:@"is_star"];
    article.is_read = [rs boolForColumn:@"is_read"];
    article.is_download = [rs boolForColumn:@"is_download"];
    return article;
}

-(NSMutableArray *) getReadedArticles
{
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=1 ORDER BY rowid desc;"];
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]){
        [articles addObject:[self getArticleInstance:rs dateFormatter:dateFormatter]];
    }
    [dateFormatter release];
    NSSortDescriptor *timeDescriptor = [[NSSortDescriptor alloc] initWithKey:@"create_time"  
                                                                   ascending:NO];  
    NSArray *sortDescriptors = [NSArray arrayWithObject:timeDescriptor];  
    [articles sortUsingDescriptors:sortDescriptors];
    [timeDescriptor release];
    return articles;
}



-(NSMutableArray *) getNotReadArticles
{
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Article WHERE is_read=0 ORDER BY rowid desc;"]; 
    NSMutableArray *articles = [[[NSMutableArray alloc] init] autorelease];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]){
        [articles addObject:[self getArticleInstance:rs dateFormatter:dateFormatter]];
    }
    [dateFormatter release];
    NSSortDescriptor *timeDescriptor = [[NSSortDescriptor alloc] initWithKey:@"create_time"  
                                                                   ascending:NO];  
    NSArray *sortDescriptors = [NSArray arrayWithObject:timeDescriptor];  
    [articles sortUsingDescriptors:sortDescriptors];
    [timeDescriptor release];
    return articles;
}


-(NSMutableArray *) getAllCategory
{
    NSMutableArray *categorys = [[[NSMutableArray alloc] init] autorelease];
    FMResultSet *rs = [_db executeQuery:@"SELECT * FROM Category"];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    while ([rs next]) {
        Category *category = [[Category alloc] init];
        category.name = [rs stringForColumn:@"name"];
        NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Article WHERE category='%@'", [rs stringForColumn:@"name"]];
        FMResultSet *qs = [_db executeQuery:sql];
        NSMutableArray *articles = [[NSMutableArray alloc] init];
        while ([qs next]){
            [articles addObject:[self getArticleInstance:qs dateFormatter:dateFormatter]];
        }
        category.articles = articles;
        [categorys addObject:category];
        [category release];
        [articles release];
    }
    [rs close];
    [dateFormatter release];
    return categorys;
}

-(NSMutableArray *) getAllImage:(NSString *)key
{
    NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Image WHERE key='%@'",key];
    FMResultSet *rs = [_db executeQuery:sql];
    NSMutableArray *images = [[[NSMutableArray alloc] init] autorelease];
    while ([rs next]){
        [images addObject:[rs stringForColumn:@"url"]];
    }
    return images;

}

-(NSMutableArray *)getRecentArticles
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    NSMutableArray *articles = [[NSMutableArray alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    FMResultSet *rs = [_db executeQuery:@"SELECT key FROM History"];
    while ([rs next]) {
        NSString *sql = [NSString stringWithFormat:@"SELECT * FROM Article WHERE key='%@'", [rs stringForColumn:@"key"]];
        FMResultSet *qs = [_db executeQuery:sql];
        while ([qs next]){
            [articles addObject:[self getArticleInstance:qs dateFormatter:dateFormatter]];
        }
    }
    [dateFormatter release];
    return articles;
}

-(int)getHistoryCount
{
    return [_db intForQuery:@"SELECT count(*) FROM History"];
}

- (void)closeDB
{   
    [_db close];
}
@end
