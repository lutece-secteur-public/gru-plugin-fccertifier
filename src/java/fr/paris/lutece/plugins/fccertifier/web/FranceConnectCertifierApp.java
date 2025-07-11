/*
 * Copyright (c) 2002-2023, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.fccertifier.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.fccertifier.business.FcIdentity;
import fr.paris.lutece.plugins.fccertifier.dataclient.UserDataClient;
import fr.paris.lutece.plugins.fccertifier.service.CertifierService;
import fr.paris.lutece.plugins.fccertifier.service.CertifierService.ValidationStatut;
import fr.paris.lutece.plugins.fccertifier.service.ValidationResult;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.oauth2.modules.franceconnect.business.UserInfo;
import fr.paris.lutece.plugins.oauth2.modules.franceconnect.business.service.FranceConnectService;
import fr.paris.lutece.plugins.verifybackurl.service.AuthorizedUrlService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.util.url.UrlItem;

/**
 * FranceConnect Certifier App
 */
@Controller( xpageName = "fccertifier", pageTitleI18nKey = "fccertifier.xpage.fccertifier.pageTitle", pagePathI18nKey = "fccertifier.xpage.fccertifier.pagePathLabel" )
public class FranceConnectCertifierApp extends MVCApplication
{
    private static final long serialVersionUID = 1L;
    
    //Views
    public static final String VIEW_HOME = "home";
    public static final String VIEW_VALIDATION = "validation";
    public static final String VIEW_VALIDATION_OK = "validationOK";
    public static final String VIEW_VALIDATE_FC_DATA = "validate";
    public static final String VIEW_RECERTIFY_OR_DELETE = "recertificyOrDeleteCertification";
    
    //Templates
    private static final String TEMPLATE_HOME = "skin/plugins/identitystore/modules/fccertifier/home.html";
    private static final String TEMPLATE_VALIDATION_OK = "skin/plugins/identitystore/modules/fccertifier/validation_ok.html";
    private static final String TEMPLATE_VALIDATE_DATA = "skin/plugins/identitystore/modules/fccertifier/validate_data.html";
    private static final String TEMPLATE_RECERTIFICATE_OR_DELETE_CERTIFICATION = "skin/plugins/identitystore/modules/fccertifier/recertify_or_delete_certification.html";    
    private static final String TEMPLATE_VALIDATION_ERROR = "skin/plugins/identitystore/modules/fccertifier/validation_error.html";

    //Actions
    private static final String ACTION_FETCH_FC_DATA = "fetch";
    private static final String ACTION_CERTIFY = "certify";
    
    //Markers
    private static final String MARK_FC_INFOS = "fc_infos";
    private static final String MARK_IDENTITY = "identity";
    private static final String MARK_JSP_MYDASHBOARD = "jsp_mydashboard";
    private static final String MARK_SERVICE_URL = "service_url";
    private static final String MARK_VALIDATION_RESULT = "validationResult";
    //Properties
    private static final String PROPERTY_JSP_MYDASHBOARD = AppPropertiesService.getProperty( "fccertifier.mydashboard.identity.xpage" );
    private static final String PROPERTY_SUSPICIOUS_REDIRECT_PAGE = AppPropertiesService.getProperty( "fccertifier.identity.suspicious.france_connect.redirect.page" );
    private static final String PROPERTY_SUSPICIOUS_REDIRECT_VIEW = AppPropertiesService.getProperty( "fccertifier.identity.suspicious.france_connect.redirect.view" );
    //Parameters
    private static final String PARAMETER_BIRTHPLACE = "birthplace";
    
    private static final String DATACLIENT_USER = "user";
    private static final String URL_SUSPICIOUS_IDENTITY = "Portal.jsp?page=" + PROPERTY_SUSPICIOUS_REDIRECT_PAGE + "&view=" + PROPERTY_SUSPICIOUS_REDIRECT_VIEW;
    private final CertifierService _certifierService;

    /**
     * Constructor for init
     */
    public FranceConnectCertifierApp( )
    {
        super( );
        _certifierService = new CertifierService( );
    }

    /**
     * Gets the Home page
     *
     * @param request
     *            The HTTP request
     * @return The XPage
     * @throws UserNotSignedException
     *             if user is not connected
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage home( HttpServletRequest request ) throws UserNotSignedException
    {
        checkUserAuthentication( request );

        _certifierService.startValidation( request );

        return getXPage( TEMPLATE_HOME, LocaleService.getDefault( ), getModel( ) );
    }

    /**
     * Fetch data from FrancConnect
     *
     * @param request
     *            The HTTP request
     * @return The redirected page
     * @throws UserNotSignedException
     *             if no user is connected
     */
    @Action( ACTION_FETCH_FC_DATA )
    public XPage doFetch( HttpServletRequest request ) throws UserNotSignedException
    {
        checkUserAuthentication( request );

        String strUrl = FranceConnectService.getFranceConnectDataClientUrl(request, DATACLIENT_USER );
        return redirect( request, strUrl );
    }

    /**
     * Validate FranceConnect data
     * 
     * @param request
     *            The HTTP request
     * @return The page
     * @throws UserNotSignedException
     *             if user not signed
     */
    @View( VIEW_VALIDATE_FC_DATA )
    public XPage validationFCData( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = checkUserAuthentication( request );
        Map<String, Object> model = getModel( );

        UserInfo fcUserInfo = (UserInfo) request.getSession( ).getAttribute( UserDataClient.ATTRIBUTE_USERINFO );
        IdentityDto identity = CertifierService.getIdentity( user.getName( ) );

        model.put( MARK_FC_INFOS, new FcIdentity( fcUserInfo ) );
        model.put( MARK_IDENTITY, identity );

        return getXPage( TEMPLATE_VALIDATE_DATA, LocaleService.getDefault( ), model );

    }

    /**
     * Process Certify action
     * @param request The HTTP request
     * @return The page
     * @throws UserNotSignedException if user not signed 
     */
    @Action( ACTION_CERTIFY )
    public XPage doCertify( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser user = checkUserAuthentication( request );
        UserInfo fcUserInfo = (UserInfo) request.getSession( ).getAttribute( UserDataClient.ATTRIBUTE_USERINFO );  
                
        //Recovery of the place of birth label for users born abroad.
        String strBirthPlace = request.getParameter( PARAMETER_BIRTHPLACE );
        FcIdentity fcIdentity = new FcIdentity( fcUserInfo );

        if(StringUtils.isNotEmpty( strBirthPlace ))
        {
            CertifierService.setBirthplaceLabelInSession( strBirthPlace, request.getSession( ) );
            fcIdentity.setIdsBirthPlace( strBirthPlace );
        }    
        
        //Suspicious identity
        if( CertifierService.existStrictSuspiciousIdentities( fcIdentity, user.getName( ) ) )
        {
            return redirect( request, new UrlItem( URL_SUSPICIOUS_IDENTITY ).getUrl( ) );
        } 
        
        ValidationResult result = _certifierService.validate( request, fcUserInfo );

        if ( result != null && result.getValidationStatus( ) != ValidationStatut.OK )
        {
            //technical errors
            if( result.getValidationStatus( ) == ValidationStatut.TECHNICAL_ERROR )
            {
                Map<String,Object> model = getModel( );
                model.put( MARK_VALIDATION_RESULT, result );
                
                return getXPage( TEMPLATE_VALIDATION_ERROR, LocaleService.getDefault( ), model );
            }
            addError( result.getValidationStatus( ).getMessageKey( ), LocaleService.getDefault( ) );

            if ( result.getValidationStatus( ) == ValidationStatut.SESSION_EXPIRED )
            {
                return redirectView( request, VIEW_HOME );
            }
        }
        return redirectView( request, VIEW_VALIDATION_OK );
    }

    /**
     * Displays Validation OK page
     *
     * @param request
     *            The HTTP request
     * @return The page
     * @throws UserNotSignedException
     *             if user is not connected
     */
    @View( VIEW_VALIDATION_OK )
    public XPage validationOK( HttpServletRequest request ) throws UserNotSignedException
    {
        Map<String,Object> model = getModel( );
        checkUserAuthentication( request );
        // check back url
        String strBackUrl = AuthorizedUrlService.getInstance( ).getServiceBackUrl( request );

        if ( !StringUtils.isEmpty( strBackUrl ) )
        {
            model.put( MARK_SERVICE_URL, strBackUrl );
        }

        return getXPage( TEMPLATE_VALIDATION_OK, request.getLocale( ), model );
    }

    /**
     * check if user is authenticated
     *
     * @param request
     *            request
     * @return The connected Lutece user
     * @throws UserNotSignedException
     *             if user is not connected
     */
    private LuteceUser checkUserAuthentication( HttpServletRequest request ) throws UserNotSignedException
    {
        LuteceUser luteceUser = SecurityService.isAuthenticationEnable( ) ? SecurityService.getInstance( ).getRegisteredUser( request ) : null;

        if ( luteceUser == null )
        {
            throw new UserNotSignedException( );
        }
        return luteceUser;
    }
    
    /**
     * Gets the Recerify or delete certification page
     *
     * @param request
     *            The HTTP request
     * @return The XPage
     * @throws UserNotSignedException
     *             if user is not connected
     */
    @View( value = VIEW_RECERTIFY_OR_DELETE )
    public XPage getRecertifyOrDelete( HttpServletRequest request ) throws UserNotSignedException
    {
        checkUserAuthentication( request );
        
        Map<String,Object> model = getModel( );
        model.put( MARK_JSP_MYDASHBOARD, PROPERTY_JSP_MYDASHBOARD );
        
        return getXPage( TEMPLATE_RECERTIFICATE_OR_DELETE_CERTIFICATION, LocaleService.getDefault( ), model );
    }

}