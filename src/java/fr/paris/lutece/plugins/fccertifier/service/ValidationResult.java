/*
 * Copyright (c) 2002-2025, Mairie de Paris
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
package fr.paris.lutece.plugins.fccertifier.service;

import java.util.List;

import fr.paris.lutece.plugins.fccertifier.service.CertifierService.ValidationStatut;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeStatus;

/**
 * 
 * ValidationResult
 *
 */
public class ValidationResult
{
    private ValidationStatut _validationStatus;
    private List<AttributeStatus> attributeStatuses;
    private List<AttributeDto> listAttributes;
    
    /**
     * @return the _validationStatus
     */
    public ValidationStatut getValidationStatus( )
    {
        return _validationStatus;
    }
    
    /**
     * @param validationStatus the _validationStatus to set
     */
    public void setValidationStatus( ValidationStatut validationStatus )
    {
        this._validationStatus = validationStatus;
    }
    
    /**
     * @return the attributeStatuses
     */
    public List<AttributeStatus> getAttributeStatuses( )
    {
        return attributeStatuses;
    }
    
    /**
     * @param attributeStatuses the attributeStatuses to set
     */
    public void setAttributeStatuses( List<AttributeStatus> attributeStatuses )
    {
        this.attributeStatuses = attributeStatuses;
    }

    /**
     * @return the listAttributes
     */
    public List<AttributeDto> getListAttributes( )
    {
        return listAttributes;
    }

    /**
     * @param listAttributes the listAttributes to set
     */
    public void setListAttributes( List<AttributeDto> listAttributes )
    {
        this.listAttributes = listAttributes;
    }
    
    
    
}
