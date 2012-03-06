
#import "FirstRunViewController.h"
#import "NSURLProtocolCustom.h"
#import "sohukanAppDelegate.h"
#import "SystemTool.h"

@implementation FirstRunViewController

- (void)_authenticateUser {
    
    NSString *KEY = @"$V0[K#-AAOc/ZWyfcNQubXO8e,)?y*G&";
    NSString *passWord = _password.text;
    NSString *userName = _username.text;
    NSString *GID = @"7bcd0c8866e5fa8b7af624732ec6d6dc60d3daee";
    NSString *APPID = @"1088";
    NSString *sig = [NSString stringWithFormat:@"%@%@%@%@",_username.text, APPID, GID, KEY];
    NSString *passportURLString = @"http://internal.passport.sohu.com/mobile/mobile_gettoken.jsp";
    //NSString *urlString = @"http://192.168.95.182/mobile/mobile_gettoken.jsp";
    NSString *xmlString = @"<?xml version='1.0' encoding='GBK'?><info><userid>%@</userid><password>%@</password><appid>%@</appid><sig>%@</sig><gid>%@</gid></info>";
    NSString *myRequestString = [NSString stringWithFormat:xmlString, userName, [SystemTool md5:passWord].lowercaseString, APPID, [SystemTool md5:sig].lowercaseString, GID];
    NSLog(@"string,%@",myRequestString);
    NSData *myRequestData = [NSData dataWithBytes: [myRequestString UTF8String] length: [myRequestString length]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:passportURLString]];
    [request setHTTPMethod: @"POST"];
    [request setHTTPBody: myRequestData];
    NSData *response = [NSURLConnection sendSynchronousRequest:request returningResponse: nil error: nil];
    NSString *responseString = [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
    BOOL isError = NO;
    if ([responseString isMemberOfClass:[NSString class]]) {
        //NSString *token = [[responseString componentsSeparatedByString:@"|"] lastObject];
        //NSString *appUrlString = @"http://10.10.69.53/article/list.xml?access_token=%@";
    }else{
        isError = YES;
    }
    NSLog(@"res%@", responseString);
    [responseString release];
    [request release];
    if (isError) {  
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"用户名或者密码输入错误" 
                                                            message:@""
                                                           delegate:self 
                                                  cancelButtonTitle:@"取消" 
                                                  otherButtonTitles:@"确定",nil];
        [alert show];
        [alert release];
    }
    /*
	NSDictionary *session = [[LastFMService sharedInstance] getMobileSessionForUser:_username.text password:_password.text];
	if([[session objectForKey:@"key"] length]) {
		[[NSUserDefaults standardUserDefaults] setObject:_username.text forKey:@"lastfm_user"];
		[[NSUserDefaults standardUserDefaults] setObject:[session objectForKey:@"key"] forKey:@"lastfm_session"];
		[[NSUserDefaults standardUserDefaults] setObject:[session objectForKey:@"subscriber"] forKey:@"lastfm_subscriber"];
		[[NSUserDefaults standardUserDefaults] setObject:@"YES" forKey:@"removeUserTags"];
		[[NSUserDefaults standardUserDefaults] setObject:@"YES" forKey:@"removePlaylists"];
		[[NSUserDefaults standardUserDefaults] setObject:@"YES" forKey:@"removeLovedTracks"];
		[[NSUserDefaults standardUserDefaults] synchronize];
		[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) showProfileView:YES];
	} else {
		if([[LastFMService sharedInstance].error.domain isEqualToString:LastFMServiceErrorDomain] && [LastFMService sharedInstance].error.code == errorCodeAuthenticationFailed) {
			[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) displayError:NSLocalizedString(@"ERROR_AUTH", @"Auth error") withTitle:NSLocalizedString(@"ERROR_AUTH_TITLE", @"Auth error title")];
		} else {
			[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) reportError:[LastFMService sharedInstance].error];
		}
		_username.enabled = YES;
		_password.text = @"";
		_password.enabled = YES;
	}
     */
    sohukanAppDelegate *myDelegate = (sohukanAppDelegate *)[[UIApplication sharedApplication] delegate];
    myDelegate.window.rootViewController = myDelegate.navigationController;
    [myDelegate.window addSubview:myDelegate.navigationController.view];
    [self.view removeFromSuperview];
	[UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
}

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if (buttonIndex == alertView.cancelButtonIndex) {
        return;
    }else{
        return;
    }
}

-(void)viewWillAppear:(BOOL)animated {
	[super viewWillAppear:animated];
	_username.text = [[NSUserDefaults standardUserDefaults] objectForKey: @"lastfm_user"];

}
- (void)viewWillDisappear:(BOOL)animated {
	[super viewWillDisappear:animated];
}
-(IBAction)registerButtonPressed:(id)sender {
	_regusername.text = @"";
	_regpassword.text = @"";
	_regemail.text = @"";
	[UIView beginAnimations:nil context:nil];
	[UIView setAnimationDuration:0.75];
	[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:self.view cache:YES];
	[self.view addSubview:_registrationView];
	[UIView commitAnimations];
}
-(void)_registerUser {
	/*[[LastFMService sharedInstance] createUser:_regusername.text withPassword:_regpassword.text andEmail:_regemail.text];
	if([LastFMService sharedInstance].error) {
		if([[LastFMService sharedInstance].error.domain isEqualToString:LastFMServiceErrorDomain] && [LastFMService sharedInstance].error.code == 6) {
			[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) displayError:[[LastFMService sharedInstance].error.userInfo objectForKey:NSLocalizedDescriptionKey] withTitle:NSLocalizedString(@"ERROR_REGFAILURE_TITLE", @"Registration error title")];
		} else {
			[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) reportError:[LastFMService sharedInstance].error];
		}
		_regusername.enabled = YES;
		_regpassword.text = @"";
		_regpassword.enabled = YES;
		_regemail.enabled = YES;
	} else {
		_username.text = _regusername.text;
		_password.text = _regpassword.text;
		[self cancelButtonPressed:nil];
		[self loginButtonPressed:nil];
	}
     */
}
- (BOOL)validateEmail: (NSString *) candidate {
	NSString *emailRegex = @"[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}"; 
	NSPredicate *emailTest = [NSPredicate predicateWithFormat:@"SELF MATCHES %@", emailRegex];
	return [emailTest evaluateWithObject:candidate];
}
-(IBAction)createButtonPressed:(id)sender {
	if([_regusername.text length] && [_regpassword.text length] && [_regemail.text length] && [self validateEmail:_regemail.text]) {
		[UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
		_regusername.enabled = NO;
		_regpassword.enabled = NO;
		_regemail.enabled = NO;
		[self performSelector:@selector(_registerUser) withObject:nil afterDelay:0.1];
	} else {
		/*[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) displayError:NSLocalizedString(@"ERROR_MISSINGINFO", @"Missing info") withTitle:NSLocalizedString(@"ERROR_MISSINGINFO_TITLE", @"Missing info title")];
        */
	}	
}
-(IBAction)cancelButtonPressed:(id)sender {
	[UIView beginAnimations:nil context:nil];
	[UIView setAnimationDuration:0.75];
	[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:self.view cache:YES];
	[_registrationView removeFromSuperview];
	[UIView commitAnimations];
}
-(IBAction)loginButtonPressed:(id)sender {
	if([_username.text length] && [_password.text length]) {
		[UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
		_username.enabled = NO;
		_password.enabled = NO;
        NSLog(@"login");
		[self performSelector:@selector(_authenticateUser) withObject:nil afterDelay:0.1];
	} else {
		/*[((MobileLastFMApplicationDelegate *)[UIApplication sharedApplication].delegate) displayError:NSLocalizedString(@"ERROR_MISSINGINFO", @"Missing info") withTitle:NSLocalizedString(@"ERROR_MISSINGINFO_TITLE", @"Missing info title")];*/
	}
}
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	if(textField == _username && [_username.text length] > 0)
		[_password becomeFirstResponder];
	if(textField == _password && [_password.text length] > 0)
		[self loginButtonPressed:textField];
	if(textField == _regusername && [_regusername.text length] > 0)
		[_regpassword becomeFirstResponder];
	if(textField == _regpassword && [_regpassword.text length] > 0)
		[_regemail becomeFirstResponder];
	if(textField == _regemail && [_regemail.text length] > 0)
		[self createButtonPressed:textField];
	return NO;
}

-(void)dismissKeyboard {
	[_username resignFirstResponder];
	[_password resignFirstResponder];
}
-(void)viewDidLoad {
	
}
-(void)viewDidUnload {
	
}
- (void)dealloc {
	[super dealloc];
}
@end

@implementation FirstRunViewBackground
- (void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event {
	[delegate dismissKeyboard];
}
@end
