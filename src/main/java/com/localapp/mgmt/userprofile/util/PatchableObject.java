package com.localapp.mgmt.userprofile.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PatchableObject {
    Logger logger = LoggerFactory.getLogger(PatchableObject.class);

    public void updateValues(PatchableObject requestToUpdate, Map<String, String> valuesToUpdate) throws NoSuchElementException {
        valuesToUpdate.forEach((key, value) -> {
            try {
                BeanUtils.getProperty(requestToUpdate, key);
                BeanUtils.setProperty(requestToUpdate, key, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.error("Error occurred while patching an object ");
            } catch (NoSuchMethodException e) {
                throw new NoSuchElementException();
            }
        });
    }

}
