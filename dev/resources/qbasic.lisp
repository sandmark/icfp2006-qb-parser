(tagbody
   (defun chr (code)
     (string (code-char code)))

   (defun qprint (&rest args)
     (format t "~{~A~}" args))

   (defun at (i coll)
     (nth (- i 1) coll))

   (defun checkpass (&rest args)
     nil)

   (defun concat (&rest args)
     (format nil "~{~A~}" args))

   (defparameter username "hmonk")
   (defparameter words '(abc foobar))
   (defparameter i 1)
   (defparameter pwdcount (length words))

 START
   (defparameter zero 48)
   (defparameter nine 57)
   (defparameter one 48)
   (defparameter two 48)
   (defparameter pass "")

 HACK
   (setf pass (concat (at i words) (chr two) (chr one)))
   (qprint username "@" pass (chr 10))
   (if (checkpass username pass)
       (go SUCCESS))
   (go INC-ONE)

 INC-ONE
   (incf one)
   (if (> one nine)
       (go INC-TWO))
   (go HACK)

 INC-TWO
   (setf one zero)
   (incf two)
   (if (> two nine)
       (go INC-I))
   (go HACK)

 INC-I
   (setf one zero)
   (setf two zero)
   (incf i)
   (if (> i pwdcount)
       (go FAIL))
   (go HACK)

 FAIL
   (qprint "no matches." (chr 10))
   (go END)

 SUCCESS
   (qprint "success: " username "@" pass (chr 10))

 END)
