package gigaherz.elementsofpower.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public class KeyBindingInterceptor extends KeyBinding
{
    static List keybindArray = null;

    protected KeyBinding interceptedKeyBinding;
    private boolean interceptionActive;

    private int interceptedPressTime;

    static Field fieldKeybindArray;
    static Field fieldPressed;
    static Field fieldPressTime;

    private static Field findObfuscatedField(Class<?> clazz, String... names)
    {
        return ReflectionHelper.findField(KeyBinding.class, ObfuscationReflectionHelper.remapFieldNames(clazz.getName(), names));
    }

    private static void ensureHaveKeybindArray() throws NoSuchFieldException
    {
        if (fieldKeybindArray == null)
        {
            fieldKeybindArray = findObfuscatedField(KeyBinding.class, "keybindArray", "field_74516_a");
            fieldKeybindArray.setAccessible(true);
        }
    }

    private static void ensureHavePressed() throws NoSuchFieldException
    {
        if (fieldPressed == null)
        {
            fieldPressed = findObfuscatedField(KeyBinding.class, "pressed", "field_74513_e");
            fieldPressed.setAccessible(true);
        }
    }

    private static void ensureHavePressTime() throws NoSuchFieldException
    {
        if (fieldPressTime == null)
        {
            fieldPressTime = findObfuscatedField(KeyBinding.class, "pressTime", "field_151474_i");
            fieldPressTime.setAccessible(true);
        }
    }

    private static void setPressedAndTime(KeyBinding binding, boolean pressed, int time)
    {
        try
        {
            ensureHavePressed();
            fieldPressed.set(binding, pressed);

            ensureHavePressTime();
            fieldPressTime.set(binding, time);

        } catch (NoSuchFieldException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        } catch (IllegalAccessException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        }
    }

    private static void setPressTime(KeyBinding binding, int time)
    {
        try
        {
            ensureHavePressTime();
            fieldPressTime.set(binding, time);

        } catch (NoSuchFieldException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        } catch (IllegalAccessException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        }
    }

    private static boolean getPressed(KeyBinding binding)
    {
        try
        {
            ensureHavePressed();
            return (Boolean) fieldPressed.get(binding);

        } catch (NoSuchFieldException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        } catch (IllegalAccessException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        }
    }

    private static int getPressTime(KeyBinding binding)
    {
        try
        {
            ensureHavePressTime();
            return (Integer) fieldPressTime.get(binding);

        } catch (NoSuchFieldException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        } catch (IllegalAccessException e)
        {
            throw new ReportedException(new CrashReport("Exception updating KeyBindingInterceptor", e));
        }
    }

    private static void getKeybindArrayFromSuper()
    {
        try
        {
            ensureHaveKeybindArray();
            keybindArray = (List) fieldKeybindArray.get(null);
        } catch (NoSuchFieldException e)
        {
            throw new ReportedException(new CrashReport("Exception initializing KeyBindingInterceptor", e));
        } catch (IllegalAccessException e)
        {
            throw new ReportedException(new CrashReport("Exception initializing KeyBindingInterceptor", e));
        }
    }

    /**
     * Create an Interceptor based on an existing binding.
     * The initial interception mode is OFF.
     * If existingKeyBinding is already a KeyBindingInterceptor, a reinitialised copy will be created but no further effect.
     *
     * @param existingKeyBinding - the binding that will be intercepted.
     */
    public KeyBindingInterceptor(KeyBinding existingKeyBinding)
    {
        super(existingKeyBinding.getKeyDescription(), existingKeyBinding.getKeyCode(), existingKeyBinding.getKeyCategory());

        // the base constructor automatically adds the class to the keybindArray and hash, which we don't want, so undo it
        if (keybindArray == null)
        {
            getKeybindArrayFromSuper();
        }
        keybindArray.remove(this);

        this.interceptionActive = false;

        setPressedAndTime(this, false, 0);

        this.interceptedPressTime = 0;

        if (existingKeyBinding instanceof KeyBindingInterceptor)
        {
            interceptedKeyBinding = ((KeyBindingInterceptor) existingKeyBinding).getOriginalKeyBinding();
        } else
        {
            interceptedKeyBinding = existingKeyBinding;
        }

        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public void setInterceptionActive(boolean newMode)
    {
        if (newMode && !interceptionActive)
        {
            this.interceptedPressTime = 0;
        }
        interceptionActive = newMode;
    }

    public boolean isKeyDown()
    {
        copyKeyCodeToOriginal();
        return interceptedKeyBinding.isPressed();
    }

    /**
     * @return returns false if interception isn't active.  Otherwise, retrieves one of the clicks (true) or false if no clicks left
     */
    public boolean retrieveClick()
    {
        copyKeyCodeToOriginal();
        if (interceptionActive)
        {
            copyClickInfoFromOriginal();

            if (this.interceptedPressTime == 0)
            {
                return false;
            } else
            {
                --this.interceptedPressTime;
                return true;
            }
        } else
        {
            return false;
        }
    }

    /**
     * A better name for this method would be retrieveClick.
     * If interception is on, resets .pressed and .pressTime to zero.
     * Otherwise, copies these from the intercepted KeyBinding.
     *
     * @return If interception is on, this will return false; Otherwise, it will pass on any clicks in the intercepted KeyBinding
     */
    @Override
    public boolean isPressed()
    {
        copyKeyCodeToOriginal();
        copyClickInfoFromOriginal();

        if (interceptionActive)
        {
            setPressedAndTime(this, false, 0);
            return false;
        } else
        {
            int pressTime = getPressTime(this);
            if (pressTime == 0)
            {
                return false;
            } else
            {
                setPressTime(this, --pressTime);
                return true;
            }
        }
    }

    public KeyBinding getOriginalKeyBinding()
    {
        return interceptedKeyBinding;
    }

    protected void copyClickInfoFromOriginal()
    {
        int pressTimeIntercepted = getPressTime(interceptedKeyBinding);
        int pressTime = getPressTime(this) + pressTimeIntercepted;
        this.interceptedPressTime += pressTimeIntercepted;
        setPressTime(interceptedKeyBinding, 0);
        setPressedAndTime(this, getPressed(interceptedKeyBinding), pressTime);
    }

    protected void copyKeyCodeToOriginal()
    {
        if (this.getKeyCode() != interceptedKeyBinding.getKeyCode())
        {
            setKeyCode(interceptedKeyBinding.getKeyCode());
            resetKeyBindingArrayAndHash();
        }
    }

}
