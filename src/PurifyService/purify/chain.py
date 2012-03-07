'''
Created on 2012-2-29

@author: Leon

'''

import logging

from rulers import *

class Request:
    def __init__(self, domain, **kargs):
        self.error  = None
        self.domain = domain
        for k, v in kargs.items():
            self.__dict__[k] = v

    def setError(self, error):
        self.error = error

    def getError(self): return self.error


class Chain:
    '''
        various domain mapping to various chain.
        
    '''
    def __init__(self, name, first = None):
        self.chainName = name
        self.first = first

    def getFirst(self):
        return self.first

    def setFirst(self, first):
        self.first = first
    

class ChainManager:
    def __init__(self, session):
        self.lastFilter = session
        self.db = session.store 
        self.cc = {}
        self.logger = logging.getLogger("ChainManager")
        self.default = None

    def install(self):
        '''
            install all chains described in database
        '''
        self.logger.debug("establish Chains stored in database.")

        # build default chain for all poor guys.
        filter = ReadabilityRuler()
        filter.setNext(self.lastFilter)

        self.default = Chain("default")
        self.default.setFirst(filter)


        return True


    def determin(self, domain, **kargs):
        '''
            determin which chain is suitable for this domain 
        '''
        if not domain: return False
        if domain in self.cc:
            self.logger.debug("domain: %s" % (domain, ))
            chain = self.cc[domain]
            if chain:
                filter  = chain.getFirst()
                if filter:
                    request = Request(domain, **kargs)
                    filter.handle(request)
                    return True
                else:
                    self.logger.error("chain in domain: (%s) first filter does not existed!" % domain)
                    return False

        self.logger.debug("default chain will be utilized.")

        #TODO 
        default = self.default.getFirst()
        if default:
            request = Request(domain, **kargs)
            default.handle(request)
        else:
            self.logger.debug("default chain does not installed.")
            return False

        return True




